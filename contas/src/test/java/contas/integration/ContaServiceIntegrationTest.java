package contas.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import contas.model.Conta;
import contas.repitory.ContaRepository;
import contas.service.ContaService;
import jakarta.transaction.Transactional;

@SpringBootTest
public class ContaServiceIntegrationTest {
	

    @Autowired
    private ContaService contaService;

    @Autowired
    private ContaRepository contaRepository;

    @Test
    @Transactional
    public void testTransferenciaSemConflito() throws InterruptedException {
    	
        Conta contaOrigem = new Conta();
        contaOrigem.setId(1L);
        contaOrigem.setSaldo(200.0);
        contaRepository.save(contaOrigem);

        Conta contaDestino = new Conta();
        contaDestino.setId(2L);
        contaDestino.setSaldo(0.0);
        contaRepository.save(contaDestino);
        
        AtomicReference<Exception> exceptionThread1 = new AtomicReference<>();
        AtomicReference<Exception> exceptionThread2 = new AtomicReference<>();
        
        Thread thread1 = new Thread(() -> {
            try {
                contaService.transferir(1L, 2L, 100.0);
            } catch (Exception e) {
            	exceptionThread1.set(e);
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                contaService.transferir(1L, 2L, 50.0);
            } catch (Exception e) {
            	exceptionThread2.set(e);
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        Conta contaAtualizadaOrigem = contaRepository.findById(1L).orElseThrow();
        Conta contaAtualizadaDestino = contaRepository.findById(2L).orElseThrow();

        assertNotNull(exceptionThread1.get());
        assertTrue(exceptionThread1.get() instanceof IllegalArgumentException);
        assertEquals("Saldo insuficiente!", exceptionThread1.get().getMessage());
        
        assertNotNull(exceptionThread2.get());
        assertTrue(exceptionThread2.get() instanceof IllegalArgumentException);
        assertEquals("Saldo insuficiente!", exceptionThread2.get().getMessage());
        
        assertEquals(50.0, contaAtualizadaOrigem.getSaldo()); 
        assertEquals(150.0, contaAtualizadaDestino.getSaldo()); 
        
    }

}

package contas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import contas.model.Conta;
import contas.repitory.ContaRepository;
import jakarta.transaction.Transactional;

@Service
public class ContaService {

	@Autowired
	private ContaRepository contaRepository;

	@Transactional
	public Conta criaConta(String nome) {
		Conta conta = new Conta();
		conta.setTitular(nome);
		conta.setSaldo(0.0);
		return contaRepository.save(conta);
	}
	
	@Transactional
	public Conta visualizarConta(Long id) {
	    return contaRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found: " + id));
	}

	@Transactional
	public void creditar(Long id, double valor) {
		Conta conta = contaRepository.findById(id).orElseThrow();
		conta.setSaldo(conta.getSaldo() + valor);
		contaRepository.save(conta);
	}

	@Transactional
	public void debitar(Long id, double valor) {
		Conta conta = contaRepository.findById(id).orElseThrow();
		if (conta.getSaldo() < valor) {
			throw new IllegalArgumentException("Saldo insuficiente!");
		}
		conta.setSaldo(conta.getSaldo() - valor);
		contaRepository.save(conta);
	}

	@Transactional
	public void transferir(Long idOrigem, Long idDestino, double valor) {
		
		Conta contaOrigem = contaRepository.findById(idOrigem)
                .orElseThrow(() -> new RuntimeException("Account not found: " + idOrigem));
        
		Conta contaDestino = contaRepository.findById(idDestino)
                .orElseThrow(() -> new RuntimeException("Account not found: " + idDestino));
		
		synchronized (this) {
			debitar(contaOrigem.getId(), valor);
			creditar(contaDestino.getId(), valor);
		}
	}

}

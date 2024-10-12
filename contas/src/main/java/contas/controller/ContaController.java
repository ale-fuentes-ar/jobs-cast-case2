package contas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import contas.model.Conta;
import contas.service.ContaService;

@RestController
@RequestMapping("/contas")
public class ContaController {

	@Autowired
	private ContaService contaService;
	
	@PostMapping
	public Conta criarConta(@RequestParam String titular) {
		return contaService.criaConta(titular);
	}

    @PostMapping("/{id}/creditar")
    public void creditar(@PathVariable Long id, @RequestParam double valor) {
        contaService.creditar(id, valor);
    }

    @PostMapping("/{id}/debitar")
    public void debitar(@PathVariable Long id, @RequestParam double valor) {
        contaService.debitar(id, valor);
    }

    @PostMapping("/transferir")
    public void transferir(@RequestParam Long idOrigem, @RequestParam Long idDestino, @RequestParam double valor) {
        contaService.transferir(idOrigem, idDestino, valor);
    }
}

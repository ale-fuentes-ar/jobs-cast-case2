package contas.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import contas.model.Conta;
import contas.service.ContaService;

@RestController
@RequestMapping("/contas")
public class ContaController {

	@Autowired
	private ContaService contaService;

	@PostMapping
	public ResponseEntity<Conta> criarConta(@RequestParam String titular) {
		Conta conta = contaService.criaConta(titular);

	    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
	            .path("/{id}")
	            .buildAndExpand(conta.getId())
	            .toUri();
	    
	    return ResponseEntity.created(location).body(conta);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Conta> visualizarConta(@PathVariable Long id) {
		try {
			Conta conta = contaService.visualizarConta(id);
			return ResponseEntity.ok(conta);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
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

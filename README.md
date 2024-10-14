# Hands-On |  LAB Quarkus 2024
<img src="https://img.shields.io/badge/by-Alejandro.Fuentes-informational?style=for-the-badge&logoColor=white&color=004767" alt="" /> <img src="https://img.shields.io/badge/for-CAST_group-informational?style=for-the-badge&logoColor=white&color=004767" alt="" />

<img src="https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />

## CASE 2


#### Requisitos não funcionais

1- Criar operações básicas de manipulação de contas:

* Criar conta.
* Creditar valor em uma conta.
* Debitar valor de uma conta.
* Transferir valor entre contas.

2- Validar as operações (ex.: não permitir transferências ou débitos que
deixem a conta com saldo negativo).

### Modelo

```mermaid
classDiagram
	class Account {
		-int id 
		-String titular
		-float saldo
	}
	
	style Account fill:#cdcdcd,stroke-width:0px
```

## Testando 

observação | REST | url |
-|-|-
crear conta | POST | http://localhost:8091/contas?titular=cast-case
consultar conta | GET | http://localhost:8091/contas/1
creditar | POST | http://localhost:8091/contas/1/creditar

> Body (form-data): <br>
> valor: 100.22

observação | REST | url |
-|-|-
debitar | POST | POST http://localhost:8091/contas/1/debitar

> Body (form-data): <br>
> valor: 50.10

## Visualizar H2 console

link [h2-console][link-h2]
* JDBC URL : jdbc:h2:mem:contacastdb
* User : admin
* pass : 1234



---
## CASE 3

Penso que utilizando acesso a uma mesma conta, em tempo de uso do sistema pode dar varias operações intentem atualizar o saldo.
Para evitar isso se pode acessar a os registros bloqueando el acesso enquanto esta sendo utilizado 

> -- iniciando uma transferencia <br>
> INFO: **Starting transfer from account 1001 to account 1002 with amount 500.0** <br>
> ERROR: Insufficient funds in account: 1001 <br>
> INFO: Rolling back transaction due to error <br>
> -- iniciando uma outra transferencia <br>
> INFO: **Starting transfer from account 1003 to account 1004 with amount 100.0** <br>
> ERROR: Could not execute statement; SQL [n/a]; constraint [null]; nested exception is **org.hibernate.exception.ConstraintViolationException**: could not execute statement

```java
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Conta> findByIdWithLock(Long id);
```


<!-- links and tools -->
[link-h2]:http://localhost:8091/h2-console

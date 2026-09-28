package interfaces;

// Contrato para los pedidos que se pueden despachar.
// Lo implementan los pedidos de compra express.
public interface Despachable {
    String despachar();
}
package interfaces;

// Contrato para los pedidos que pueden cancelarse.
// Solo lo implementan los tipos de pedido que admiten cancelacion.
public interface Cancelable {
    String cancelar();
}
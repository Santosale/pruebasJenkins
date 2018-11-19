package services.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

public class PaypalClient {

	/*
	 * Guardamos como propiedades privadas de la clase el clientId y clientSecret.
	 */
	private String clientId = "AW1jnKlMWtcJ89S06Cb_3wvUC2EezkhFwKCm0oJWov6wExVxI1q6rKr1My3Hafc6s41rJc-yx-etXV1q";
	private String clientSecret = "EA68rBYMMp1OYW9Oc_IF4TIBC7_AND2M6j9baEb1-ijh7l_8qgXgZ1moX_rJjX2SKAa4wGsyCG3fp86_";
	
	public Map<String, Object> createPayment(final String sum, final int raffleId, final int amountItem, final HttpServletRequest request){
	    Map<String, Object> response;
	    Amount amount;
	    Transaction transaction;
	    List<Transaction> transactions;
	    Payer payer;
	    Payment payment;
	    String scheme, serverName, contextPath;
	    int portNumber;
	    RedirectUrls redirectUrls;
	    Payment createdPayment;
	    String redirectUrl;
	    APIContext context;
	    List<Links> links;
	    
	    /*
	     * Creamos un mapa que servir� como respuesta donde 
	     * ir� la informaci�n necesaria para completar el pago
	     */
	    response = new HashMap<String, Object>();
  
	    /*
	     * Primero de todo, debemos indica la moneda en la que
	     * se realizar� la transacci�n e indicaremos la cantidad.
	     * Dicha cantidad corresponder� con el precio del tique
	     * multiplicado por la cantidad que ha comprado del mismo.
	     */
	    amount = new Amount();
	    amount.setCurrency("EUR");
	    amount.setTotal(sum);
	    
	    /*
	     * Comenzamos la transacci�n monetaria poni�ndole
	     * el objeto amount que hemos creado previamente.
	     */
	    transaction = new Transaction();
	    transaction.setAmount(amount);
	    
	    /*
	     * La transacci�n debe ir en una lista de transacciones 
	     * por si quiere a�adir m�s de una. En nuestro caso
	     * no ser� necesario porque hemos decido que aunque
	     * compre m�s de un tique, el pago ser� �nico.
	     */
	    transactions = new ArrayList<Transaction>();
	    transactions.add(transaction);

	    /*
	     * Creamos un objeto de la clase Payer y
	     * marcamos como m�todo de pago paypal,
	     * otros modos de pago podr�an ser a trav�s
	     * de tarjeta de cr�dito (credit_card)
	     * o cuenta bancaria (bank) entre otros
	     */
	    payer = new Payer();
	    payer.setPaymentMethod("paypal");

	    /*
	     * Creamos el pago, en el cual indicaremos
	     * con el m�todo setIntent que ser� una venta
	     * y que el pago se har� a trav�s de PayPal
	     * (utilizando payer) y por �ltimo le pasamos
	     * el listado de transacciones con setTransactions.
	     */
	    payment = new Payment();
	    payment.setIntent("sale");
	    payment.setPayer(payer);
	    payment.setTransactions(transactions);
  
	    /*******************************/
        scheme = request.getScheme();
        serverName = request.getServerName();
        portNumber = request.getServerPort();
        contextPath = request.getContextPath();
	    /*******************************/
        
        /*
         * Indicamos las URLs a las que se redigir� cuando
         * se cancele la compra (setCancelUrl) o cuando termine
         * (setReturnUrl)
         */
	    redirectUrls = new RedirectUrls();
	    redirectUrls.setCancelUrl(scheme+"://"+serverName+":"+portNumber+contextPath+"/raffle/display.do?raffleId="+raffleId);
	    redirectUrls.setReturnUrl(scheme+"://"+serverName+":"+portNumber+contextPath+"/ticket/user/completepayment.do?raffleId="+raffleId+"&amount="+amountItem);
	    payment.setRedirectUrls(redirectUrls);
	    
	    try {
	        redirectUrl = "";
	        /*
	         * Creamos el contexto de la aplicaci�n con el
	         * que se nos generar� una URL que dirigir� al
	         * usuario a una vista donde poder realizar
	         * el pago autentic�ndose en PayPal.
	         */
	        context = new APIContext(clientId, clientSecret, "sandbox");
	        createdPayment = payment.create(context);
	        if(createdPayment!=null){
	            links = createdPayment.getLinks();
	            for (Links link:links) {
	                if(link.getRel().equals("approval_url")){
	                    redirectUrl = link.getHref();
	                    break;
	                }
	            }
	            response.put("status", "success");
	            response.put("redirect_url", redirectUrl);
	        }
	    } catch (PayPalRESTException e) {
	        throw new IllegalArgumentException("Error happened during payment creation!");
	    }
	    return response;
	}
	
	public Map<String, Object> completePayment(final String paymentId, final String PayerId){
	    Map<String, Object> response;
	    Payment payment;
	    PaymentExecution paymentExecution;
	    APIContext context;
	    Payment createdPayment;
	    
	    /*
	     * Creamos un mapa que devolveremos con los objetos
	     * creados en este m�todo.
	     */
	    response = new HashMap<String, Object>();
	    
	    /*
	     * Creamos un objeto de la clase payment
	     * y le ponemos el identificador de pago
	     * que nos gener� PayPal al terminar en la
	     * vista de pago.
	     */
	    payment = new Payment();
	    payment.setId(paymentId);

	    /*
	     * Realizamos lo mismo pero esta vez con el
	     * identificador del pagador.
	     */
	    paymentExecution = new PaymentExecution();
	    paymentExecution.setPayerId(PayerId);
	    try {
	        context = new APIContext(clientId, clientSecret, "sandbox");
	        /*
	         * Ejecutamos el pago para hacerlo efectivo y
	         * que se descuente el dinero en la cuenta de
	         * PayPal del usuario.
	         */
	        createdPayment = payment.execute(context, paymentExecution);
	        if(createdPayment!=null){
	            response.put("status", "success");
	            response.put("payment", createdPayment);
	        }
	    } catch (PayPalRESTException e) {
	    	throw new IllegalArgumentException(e);
	    }
	    return response;
	}
	
	
}

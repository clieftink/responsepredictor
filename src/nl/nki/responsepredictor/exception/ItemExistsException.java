package nl.nki.responsepredictor.exception;

/***
 * Exception thrown in for example the case of trying to add and edge that already exists.
 * 
 * @author Cor Lieftink
 * 
 *
 */

public class ItemExistsException  extends Exception {
	
	public ItemExistsException(String msg) {
		super(msg);
	}



}

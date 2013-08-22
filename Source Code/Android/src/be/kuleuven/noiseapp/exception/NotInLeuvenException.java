/**
 * 
 */
package be.kuleuven.noiseapp.exception;

/**
 * @author Phille
 *
 */
public class NotInLeuvenException extends Exception {

	
	private static final long serialVersionUID = -3094647932610300443L;
	private String message;
	
	public NotInLeuvenException(String errorMsg){
		this.setMessage(errorMsg);
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}

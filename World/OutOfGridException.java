package World;

public class OutOfGridException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final String err = "Warning: Object outside grid. Kill it with fire!";

	@Override
	public String getMessage() {
		return err;
	}

	@Override
	public String toString() {
		return err;
	}
}

package ca.bc.gov.educ.pen.model;

public class SoundexItem {

	private String stringValue;
	private String encodedValue;

	public SoundexItem() {
		super();
	}

	public SoundexItem(String stringValue) {
		super();
		this.stringValue = stringValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public String getEncodedValue() {
		return encodedValue;
	}

	public void setEncodedValue(String encodedValue) {
		this.encodedValue = encodedValue;
	}

}

package edmai;

/**
 * This interface represents a subset the
 * <a href="https://developers.google.com/apps-script/reference/spreadsheet/range">Google Sheets
 * Range class</a>.
 *
 * @author Rob Oaks
 */
public interface GoogleRange
{
	/**
	 * This method corresponds to the Google Sheets <a href=
	 * "https://developers.google.com/apps-script/reference/spreadsheet/range#getValues()">getValues()</a>
	 * method.
	 *
	 * @return
	 */
	public Object[][] getValues();


	/**
	 * This method corresponds to the Google Sheets <a href=
	 * "https://developers.google.com/apps-script/reference/spreadsheet/range#setValues(Object)">setValues</a>
	 * method.
	 *
	 * @return
	 */
	public GoogleRange setValues(Object[][] values);
}

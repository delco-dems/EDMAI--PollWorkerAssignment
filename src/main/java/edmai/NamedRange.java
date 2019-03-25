package edmai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is a mock class that is intended to represent a spreadsheet range object with rows and
 * columns. In practice, of course, referencing code will use the actual NamedRange object
 * corresponding for the Google Sheets object model.
 *
 * @author Rob Oaks
 *
 */
public class NamedRange implements Iterable<NamedRange.Row>
{
	List<Row> rows;


	public NamedRange()
	{
		this.rows = new ArrayList<>();
	}


	public class Row
	{
		List<Object> columns;


		public Row()
		{
			this.columns = new ArrayList<>();
		}


		public Object column(int index)
		{
			return (this.columns.get(index));
		}

	}


	@Override
	public Iterator<NamedRange.Row> iterator()
	{
		return this.rows.iterator();
	}
}

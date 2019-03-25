package edmai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is a mock class that is intended to represent a spreadsheet range object with rows and
 * columns. In practice, of course, referencing code will use the actual named range object defined
 * by the Google Sheets object model.
 *
 * @author Rob Oaks
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


		public Object getColumn(int index)
		{
			return (this.columns.get(index));
		}


		public int numColumns()
		{
			return (this.columns.size());
		}


		public Object setColumn(int index, Object element)
		{
			return (this.columns.set(index, element));
		}
	}


	public Row getRow(int index)
	{
		Row ret = this.rows.get(index);

		return (ret);
	}


	@Override
	public Iterator<NamedRange.Row> iterator()
	{
		return this.rows.iterator();
	}


	public int numRows()
	{
		return (this.rows.size());
	}
}

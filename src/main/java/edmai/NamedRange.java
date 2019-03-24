package edmai;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

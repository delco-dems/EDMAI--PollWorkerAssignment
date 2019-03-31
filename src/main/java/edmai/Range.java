package edmai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is a wrapper for the
 * <a href="https://developers.google.com/apps-script/reference/spreadsheet/range">Google Sheets
 * Range class</a>. It allows the Google {@code Range} class details to be abstracted from the
 * remainder of the implementation.
 *
 * @author Rob Oaks
 */
public class Range implements Iterable<Range.Row>
{
	GoogleRange googleRange;
	List<Row> rows;


	public Range(GoogleRange googleRange)
	{
		this.googleRange = googleRange;

		this.rows = new ArrayList<>();

		Object[][] values = this.googleRange.getValues();

		for (int rowIndex = 0; rowIndex < values.length; rowIndex++)
		{
			Row row = new Row(values[rowIndex]);
			this.rows.add(row);
		}

		this.rows = new ArrayList<>();
	}


	public class Row
	{
		List<Object> columns;


		public Row(Object[] values)
		{
			this.columns = new ArrayList<>();

			for (int columnIndex = 0; columnIndex < values.length; columnIndex++)
			{
				Object column = values[columnIndex];
				this.columns.add(column);
			}
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
	public Iterator<Range.Row> iterator()
	{
		return this.rows.iterator();
	}


	public int numRows()
	{
		return (this.rows.size());
	}


	/**
	 * Saves the entire collection of values for this range to the corresponding Google range.
	 *
	 * @return
	 */
	public GoogleRange save()
	{
		Object[][] values = new Object[this.numRows()][this.getRow(0).numColumns()];

		int rowIndex = 0;
		for (Row row : this.rows)
		{
			int columnIndex = 0;

			for (Object column : row.columns)
			{
				values[rowIndex][columnIndex] = column;
				columnIndex++;
			}

			rowIndex++;
		}

		GoogleRange ret = this.googleRange.setValues(values);

		return (ret);
	}
}

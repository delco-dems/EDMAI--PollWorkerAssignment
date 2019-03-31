package edmai;

import java.util.Map;

import com.google.common.base.Objects;

/**
 * The collection of all {@link Municipality} objects.
 *
 * @author Rob Oaks
 */
public class Municipalities
{
	Map<String, Municipality> municipalityMap;


	Municipalities(Range municipalityRange)
	{
		for (Range.Row row : municipalityRange)
		{
			String name = row.getColumn(0).toString();
			Municipality muni = new Municipality(name, (int)row.getColumn(1), (int)row.getColumn(2));
			this.municipalityMap.put(name, muni);
		}
	}


	/*
	 * Defines a municipality including the municipality name, priority, and zone number.
	 *
	 * @author Rob Oaks
	 */
	public class Municipality
	{
		String name;
		int priority;
		int zone;


		public Municipality(String name, int priority, int zone)
		{
			this.name = name;
			this.priority = priority;
			this.zone = zone;
		}


		@Override
		public boolean equals(final Object other)
		{
			if (!(other instanceof Municipality))
				return false;
			Municipality castOther = (Municipality)other;
			return Objects.equal(this.name, castOther.name);
		}


		public String getName()
		{
			return (this.name);
		}


		public int getPriority()
		{
			return (this.priority);
		}


		public int getZone()
		{
			return (this.zone);
		}


		@Override
		public int hashCode()
		{
			return Objects.hashCode(this.name);
		}
	}


	/**
	 * Returns the {@link Municipality} for the specified municipality name.
	 *
	 * @param name
	 * @return
	 */
	Municipality get(String name)
	{
		Municipality ret = this.municipalityMap.get(name);
		return (ret);
	}
}

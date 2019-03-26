package edmai;

import java.util.Map;

/**
 * The collection of all {@link Municipality} objects.
 *
 * @author Rob Oaks
 */
public class Municipalities
{
	Map<String, Municipality> municipalityMap;


	Municipalities(NamedRange municipalityRange)
	{
		for (NamedRange.Row row : municipalityRange)
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

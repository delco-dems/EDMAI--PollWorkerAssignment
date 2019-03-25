package edmai;

import java.util.Map;

/**
 * Container class for municipality info, including the municipality name, priority, and zoneNumber. A
 * {@code Municipality} object can be retrieved by name.
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


	Municipality get(String name)
	{
		Municipality ret = this.municipalityMap.get(name);
		return (ret);
	}
}

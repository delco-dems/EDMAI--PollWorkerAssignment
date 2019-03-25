package edmai;

import java.util.Map;

public class Municipalities
{
	Map<String, Municipality> municipalityMap;


	Municipalities(NamedRange municipalityRange)
	{
		// load `municipalityList` from `municipalityRange`
		for (NamedRange.Row row : municipalityRange)
		{
			String name = row.column(1).toString();
			Municipality muni = new Municipality(name, (int)row.column(2), (int)row.column(3));
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

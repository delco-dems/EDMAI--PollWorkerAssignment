package edmai;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableBiMap;

/**
 * Defines classes and methods related to the information on the spreadsheet {@code Configuration}
 * tab.
 *
 * @author Rob Oaks
 */
public class Configuration
{
	private static final ImmutableBiMap<String, Integer> shiftMap =
		ImmutableBiMap.<String, Integer> builder()
			.put("6:45 AM - 10 AM", 0)
			.put("10AM - 1 PM", 1)
			.put("1 PM - 4 PM", 2)
			.put("4 PM - 8 PM (includes staying for the vote count)", 3)
			.build();

	private final int numZones;
	private final int proximateZoneDistance;
	private final List<Zone> zoneList;

	private final int numShifts;
	private final List<ShiftConfig> shiftConfigList;


	/**
	 * The ranges below correspond to ranges that defined in the spreadsheet.
	 *
	 * @param proximateZoneDistanceRange
	 * @param zoneConfigRange
	 * @param shiftConfigRange
	 */
	Configuration(Range proximateZoneDistanceRange, Range zoneConfigRange, Range shiftConfigRange)
	{
		this.numZones = zoneConfigRange.numRows();
		this.proximateZoneDistance = (int)proximateZoneDistanceRange.getRow(0).getColumn(0);
		this.zoneList = this.loadZoneList(zoneConfigRange);

		this.numShifts = shiftConfigRange.numRows();
		this.shiftConfigList = this.loadShiftConfigList(shiftConfigRange);
	}


	/**
	 * Contains information corresponding to the spreadsheet {@code ShiftConfig} named range.
	 *
	 * @author Rob Oaks
	 */
	class ShiftConfig
	{
		int shiftNumber;
		String startTime;
		String endTime;
		String info;

		float weight;


		public ShiftConfig(int shiftNumber, String startTime, String endTime, String info, float weight)
		{
			this.shiftNumber = shiftNumber;
			this.startTime = startTime;
			this.endTime = endTime;
			this.info = info;
			this.weight = weight;
		}


		public String getEndTime()
		{
			return (this.endTime);
		}


		public String getInfo()
		{
			return (this.info);
		}


		public int getShiftNumber()
		{
			return (this.shiftNumber);
		}


		public String getStartTime()
		{
			return (this.startTime);
		}


		public float getWeight()
		{
			return (this.weight);
		}
	}

	/**
	 * Contains information corresponding to the spreadsheet {@code ZoneConfig} named range.
	 *
	 * @author Rob Oaks
	 */
	class Zone
	{
		int zoneNumber;
		List<Integer> proximityList;


		public Zone(int zoneNumber, List<Integer> proximityList)
		{
			this.zoneNumber = zoneNumber;
			this.proximityList = proximityList;
		}


		/**
		 * Returns the zoneNumber distance between this {@code Zone} and the specified zoneNumber.
		 *
		 * @param zoneNumber
		 * @return
		 */
		private int distanceInZones(int zoneNumber)
		{
			int ret = this.proximityList.get(zoneNumber);

			return (ret);
		}


		/**
		 * Is this zone proximate to the specified zone?
		 *
		 * @param zoneNumber
		 * @return
		 */
		public boolean isZoneProximate(int zoneNumber)
		{
			boolean ret = this.distanceInZones(zoneNumber) <= Configuration.this.proximateZoneDistance;

			return (ret);
		}
	}


	/**
	 * Given a shift string (e.g. "6:45 AM - 10 AM"), return a shift number.
	 *
	 * @param shiftString
	 * @return
	 */
	public static int getShiftNumber(String shiftString)
	{
		return (Configuration.shiftMap.get(shiftString));
	}


	/**
	 * Given a multivalue shifts string (e.g. "6:45 AM - 10 AM, 10AM - 1 PM"), return a list of
	 * shift numbers.
	 *
	 * @param shiftsString
	 * @return
	 */
	public static List<Integer> getShiftNumbers(String shiftsString)
	{
		List<String> shiftStringList = Configuration.splitMultivalueString(shiftsString);

		List<Integer> ret = new ArrayList<>();

		for (String shiftString : shiftStringList)
		{
			ret.add(Configuration.getShiftNumber(shiftString));
		}

		return (ret);
	}


	public static List<String> splitMultivalueString(String multivalueString)
	{
		List<String> ret = Splitter.on(',').omitEmptyStrings().trimResults().splitToList(multivalueString);

		return (ret);
	}


	private List<ShiftConfig> loadShiftConfigList(Range shiftConfigRange)
	{
		List<ShiftConfig> ret = new ArrayList<>();

		for (int i = 0; i < this.numShifts; i++)
		{
			String startTime = shiftConfigRange.getRow(i).getColumn(0).toString();
			String endTime = shiftConfigRange.getRow(i).getColumn(1).toString();
			String info = shiftConfigRange.getRow(i).getColumn(2).toString();
			float weight = Float.parseFloat(shiftConfigRange.getRow(i).getColumn(3).toString());
			ShiftConfig shiftConfig = new ShiftConfig(i, startTime, endTime, info, weight);
			ret.add(shiftConfig);
		}

		return (ret);
	}


	private List<Zone> loadZoneList(Range zoneConfigRange)
	{
		List<Zone> ret = new ArrayList<>();

		for (int i = 0; i < this.numZones; i++)
		{
			List<Integer> proximityList = new ArrayList<>();

			for (int j = 0; j < this.numZones; j++)
			{
				int proximity = Integer.parseInt(zoneConfigRange.getRow(i).getColumn(j).toString());
				proximityList.add(proximity);
			}

			Zone zoneConfig = new Zone(i, proximityList);

			ret.add(zoneConfig);
		}

		return (ret);
	}


	/**
	 * Are the two zones proximate?
	 *
	 * @param zoneNumber1
	 * @param zoneNumber2
	 * @return
	 */
	public boolean areZonesProximate(int zoneNumber1, int zoneNumber2)
	{
		Zone zoneConfig = this.zoneList.get(zoneNumber1);

		boolean ret = zoneConfig.isZoneProximate(zoneNumber2);

		return (ret);
	}


	/**
	 * Calculate the number of slots for the specified shift in accordance with the specified number
	 * of slots allocated for the busiest shift. The calculation uses the <i>weight</i> of the
	 * specified shift.
	 *
	 * @param shiftNumber
	 * @param busiestShiftNumSlots
	 * @return
	 */
	public int calculateNumShiftSlots(int shiftNumber, int busiestShiftNumSlots)
	{
		float weight = this.getShiftWeight(shiftNumber);
		int ret = Math.round(weight * busiestShiftNumSlots);

		return (ret);
	}


	/**
	 * Calculate the priority of the specified shift in accordance with the specified priority of
	 * the busiest shift. The calculation uses the <i>weight</i> of the specified shift.
	 *
	 * @param shiftNumber
	 * @param busiestShiftPriority
	 * @return
	 */
	public int calculateShiftPriority(int shiftNumber, int busiestShiftPriority)
	{
		float weight = this.getShiftWeight(shiftNumber);
		int ret = Math.round(weight * busiestShiftPriority);

		return (ret);
	}


	public int getNumShifts()
	{
		return (this.numShifts);
	}


	/**
	 * Return the weight of the specified shift. Note that shift weights are always relative to the
	 * other shifts at the associated poll; they do not relate to the weights of shifts at other
	 * polls.
	 *
	 * @param shiftNumber
	 * @return
	 */
	public float getShiftWeight(int shiftNumber)
	{
		float ret = this.shiftConfigList.get(shiftNumber).getWeight();

		return (ret);
	}
}

package edmai;

import java.util.List;

import org.joda.time.DateTime;

public class Configuration
{
	List<ShiftConfig> shiftConfigList;
	List<ZoneConfig> zoneConfigList;
	int proximateZoneDistance;
	int numShifts;


	Configuration(	NamedRange proximateZoneDistanceRange, NamedRange zoneConfigRange, NamedRange numShiftsRange,
					NamedRange shiftConfigRange)
	{
		/*
		 * TODO: read from `zoneConfigRange` and `shiftConfigRange` to construct `shiftConfigList`
		 * and `zoneConfigList`. Set `proximateZoneDistance` and `numShifts` from respective ranges.
		 */
	}


	class ShiftConfig
	{
		int shiftNumber;
		DateTime startTime;
		DateTime endTime;
		String info;
		float weight;


		public ShiftConfig(int shiftNumber, DateTime startTime, DateTime endTime, String info, float weight)
		{
			this.shiftNumber = shiftNumber;
			this.startTime = startTime;
			this.endTime = endTime;
			this.info = info;
			this.weight = weight;
		}


		/**
		 * Given a shift string (e.g. "6:45 AM - 10 AM"), return a shift number.
		 *
		 * @param shiftString
		 * @return
		 */
		public int getShiftNumber(String shiftString)
		{
			int ret = 0;

			// TODO: ??

			return (ret);
		}
	}

	class ZoneConfig
	{
		int zone;
		List<Integer> proximityList;


		public ZoneConfig(int zone, List<Integer> proximityList)
		{
			this.zone = zone;
			this.proximityList = proximityList;
		}


		private int distanceInZones(int zone)
		{
			int ret = this.proximityList.get(zone);

			return (ret);
		}


		/**
		 * Is this zone proximate to the specified zone?
		 *
		 * @param zone
		 * @return
		 */
		public boolean isZoneProximate(int zone)
		{
			boolean ret = this.distanceInZones(zone) <= Configuration.this.proximateZoneDistance;

			return (ret);
		}
	}


	/**
	 * Are the two zones proximate?
	 *
	 * @param zone1
	 * @param zone2
	 * @return
	 */
	public boolean areZonesProximate(int zone1, int zone2)
	{
		ZoneConfig zoneConfig = this.zoneConfigList.get(zone1);

		boolean ret = zoneConfig.isZoneProximate(zone2)

		return (ret);
	}
}

package edmai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import edmai.Municipalities.Municipality;
import edmai.Polls.Poll;

public class PollWorkers implements Iterable<PollWorkers.PollWorker>
{
	private final List<PollWorker> pollWorkerList;
	private final Configuration configuration;


	PollWorkers(NamedRange pollWorkerRange, Polls polls, Municipalities municipalities, Configuration configuration)
	{
		this.pollWorkerList = new ArrayList<>();
		this.configuration = configuration;

		for (NamedRange.Row row : pollWorkerRange)
		{
			String email = row.getColumn(0).toString();
			Municipality municipality = municipalities.get(row.getColumn(4).toString());
			List<EdRole> edRoles = splitEdRoles(row.getColumn(5).toString());
			TravelFlexibility travelFlexibility = TravelFlexibility.from(row.getColumn(6).toString());

			Poll homePoll = null;
			Object homePollString = row.getColumn(7);
			if (homePollString != null)
			{
				homePoll = polls.get(homePollString.toString());
			}

			List<Integer> shifts = PollWorkers.splitShifts(row.getColumn(8).toString());

			PollWorker pollWorker = new PollWorker(email, municipality, edRoles, travelFlexibility, homePoll, shifts);
			this.pollWorkerList.add(pollWorker);
		}
	}


	public enum EdRole
	{
		POLL_GREETER, POLL_WATCHER, ELECTION_PROTECTION, GOTV, DRIVER, WHATEVER, OTHER;

		private static final ImmutableMap<String, EdRole> edRoleMap =
			ImmutableMap.<String, EdRole> builder()
				.put("Poll Greeter", POLL_GREETER)
				.put("Poll Watcher (Delco residents only)", POLL_WATCHER)
				.put("Election Protection (must be an attorney, but don't need to live in Delco)", ELECTION_PROTECTION)
				.put("GOTV (voter outreach on Election Day)", GOTV)
				.put("Driver", DRIVER)
				.put("Whatever you need me to do", WHATEVER)
				.build();


		public static EdRole from(String edRoleString)
		{
			EdRole ret = EdRole.edRoleMap.get(edRoleString);
			if (ret == null)
			{
				ret = EdRole.OTHER;
			}

			return (ret);
		}
	}

	public class PollWorker
	{
		String email;
		Municipality municipality;
		List<EdRole> edRoles;
		TravelFlexibility travelFlexibility;
		Poll homePoll; // only used if `travelFlexibility = MY_POLL_ONLY`
		List<Integer> shiftNumbers;


		public PollWorker(	String email, Municipality municipality, List<EdRole> edRoles,
							TravelFlexibility travelFlexibility, @Nullable Poll homePoll, List<Integer> shiftNumbers)
		{
			this.email = email;
			this.municipality = municipality;
			this.edRoles = edRoles;
			this.travelFlexibility = travelFlexibility;
			this.homePoll = homePoll;
			this.shiftNumbers = shiftNumbers;
		}


		public List<EdRole> getEdRoles()
		{
			return (this.edRoles);
		}


		public String getEmail()
		{
			return (this.email);
		}


		public Poll getHomePoll()
		{
			return (this.homePoll);
		}


		public Municipality getMunicipality()
		{
			return (this.municipality);
		}


		public List<Integer> getShiftNumbers()
		{
			return (this.shiftNumbers);
		}


		public TravelFlexibility getTravelFlexibility()
		{
			return (this.travelFlexibility);
		}


		/**
		 * If this {@code PollWorker} has more than one shift, this method removes the lowest weight
		 * shift and return {@code true}. It returns {@code false} otherwise.
		 *
		 * @return
		 */
		public boolean removeLowestWeightShift()
		{
			boolean ret;

			if (this.shiftNumbers.size() >= 2)
			{
				int lowestWeightShiftNumber = -1;
				float lowestShiftWeight = 100.0f;
				for (int shiftNumber : this.shiftNumbers)
				{
					float shiftWeight = PollWorkers.this.configuration.getShiftWeight(shiftNumber);
					if (shiftWeight < lowestShiftWeight)
					{
						lowestShiftWeight = shiftWeight;
						lowestWeightShiftNumber = shiftNumber;
					}
				}

				this.shiftNumbers.remove(this.shiftNumbers.indexOf(lowestWeightShiftNumber));
				ret = true;
			}
			else
			{
				ret = false;
			}

			return (ret);
		}
	}

	public enum TravelFlexibility
	{
		ANYWHERE_IN_COUNTY, ANYWHERE_IN_MUNICIPALITY, MY_POLL_ONLY;

		private static final ImmutableMap<String, TravelFlexibility> travelFlexibilityMap =
			ImmutableMap.<String, TravelFlexibility> builder()
				.put("Anywhere in the county", ANYWHERE_IN_COUNTY)
				.put("Anywhere in my municipality", ANYWHERE_IN_MUNICIPALITY)
				.put("Only at my polling location", MY_POLL_ONLY)
				.build();


		public static TravelFlexibility from(String travelFlexibilityString)
		{
			TravelFlexibility ret = TravelFlexibility.travelFlexibilityMap.get(travelFlexibilityString);

			return (ret);
		}
	}


	private static List<EdRole> splitEdRoles(String edRolesString)
	{
		List<String> roleStringList = Configuration.splitMultivalueString(edRolesString);
		List<EdRole> ret = new ArrayList<>();

		for (String roleString : roleStringList)
		{
			ret.add(EdRole.from(roleString));
		}

		return (ret);
	}


	private static List<Integer> splitShifts(String shiftsString)
	{
		List<String> shiftStringList = Configuration.splitMultivalueString(shiftsString);
		List<Integer> ret = new ArrayList<>();

		for (String shiftString : shiftStringList)
		{
			ret.add(Configuration.getShiftNumber(shiftString));
		}

		return (ret);
	}


	@Override
	public Iterator<PollWorker> iterator()
	{
		return null;
	}
}

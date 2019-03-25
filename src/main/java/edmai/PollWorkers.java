package edmai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

import edmai.Municipalities.Municipality;
import edmai.Polls.Poll;

public class PollWorkers implements Iterable<PollWorkers.PollWorker>
{
	Map<String, PollWorker> pollWorkerMap;


	PollWorkers(NamedRange pollWorkerRange, Polls polls, Municipalities municipalities, Configuration configuration)
	{
		for (NamedRange.Row row : pollWorkerRange)
		{
			String email = row.column(1).toString();
			Municipality municipality = municipalities.get(row.column(5).toString());
			List<EdRole> edRoles = splitEdRoles(row.column(6).toString());
			TravelFlexibility travelFlexibility = TravelFlexibility.from(row.column(7).toString());
			Poll homePoll = polls.get(row.column(8).toString());
			List<Integer> shifts = this.splitShifts(row.column(9).toString());
			PollWorker pollWorker = new PollWorker(email, municipality, edRoles, travelFlexibility, homePoll, shifts);
			this.pollWorkerMap.put(email, pollWorker);
		}
	}


	public enum EdRole
	{
		POLL_GREETER, POLL_WATCHER, ELECTION_PROTECTION, GOTV, DRIVER, WHATEVER, OTHER;

		private static final ImmutableMap<String, EdRole> edRoleMap =
			ImmutableMap.of("Poll Greeter", POLL_GREETER,
				"Poll Watcher (Delco residents only)", POLL_WATCHER,
				"Election Protection (must be an attorney, but don't need to live in Delco)", ELECTION_PROTECTION,
				"GOTV (voter outreach on Election Day)", GOTV,
				"Driver", DRIVER,
				"Whatever you need me to do", WHATEVER);


		public static EdRole from(String edRoleString)
		{
			EdRole ret = null;

			// TODO: ??

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
							TravelFlexibility travelFlexibility, Poll homePoll, List<Integer> shiftNumbers)
		{
			this.email = email;
			this.municipality = municipality;
			this.edRoles = edRoles;
			this.travelFlexibility = travelFlexibility;
			this.homePoll = homePoll;
			this.shiftNumbers = shiftNumbers;
		}


		/**
		 * Fill in the actual {@code Poll--Calculated} and {@code Shifts--Calculated} cells for this
		 * {@code PollWorker}
		 *
		 * @param poll
		 */
		public void assign(Poll poll)
		{
			// TODO: implement
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
		 * Returns a copy of this {@code PollWorker} with the lowest weight shift removed.
		 *
		 * @return
		 */
		public PollWorker removeLowestWeightShift()
		{
			PollWorker ret = null;

			// TODO: implement

			return (ret);
		}
	}

	public enum TravelFlexibility
	{
		ANYWHERE_IN_COUNTY, ANYWHERE_IN_MUNICIPALITY, MY_POLL_ONLY;

		public static TravelFlexibility from(String travelFlexibilityString)
		{
			TravelFlexibility ret = null;

			// TODO: implement

			return (ret);
		}
	}


	private static List<EdRole> splitEdRoles(String edRolesString)
	{
		List<String> roleStringList = Splitter.on(',').splitToList(edRolesString);
		List<EdRole> ret = new ArrayList<>();

		for (String roleString : roleStringList)
		{
			ret.add(EdRole.from(roleString));
		}

		return (ret);
	}


	private List<Integer> splitShifts(String shiftsString)
	{
		List<Integer> ret = null;

		/*
		 * TODO: split `shiftsString` and, using ShiftConfig.getShiftNumber, return a list of shift
		 * numbers
		 */

		return (ret);
	}


	@Override
	public Iterator<PollWorker> iterator()
	{
		return null;
	}
}

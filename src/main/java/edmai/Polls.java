package edmai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import edmai.Municipalities.Municipality;
import edmai.PollWorkers.PollWorker;

public class Polls
{
	Map<String, Poll> pollMap;
	Configuration configuration;


	Polls(NamedRange pollNamedRange, Municipalities municipalities, Configuration configuration)
	{
		this.pollMap = new HashMap<>();
		this.configuration = configuration;

		for (NamedRange.Row row : pollNamedRange)
		{
			Municipality muni = municipalities.get(row.column(1).toString());
			String id = row.column(5).toString();
			int busiestShiftNumSlots = (int)row.column(6);
			int busiestShiftPriority = (int)row.column(7);
			Poll poll = new Poll(muni, id, busiestShiftNumSlots, busiestShiftPriority);
			this.pollMap.put(id, poll);
		}
	}


	public class Poll
	{
		Municipality municipality;
		String identifier; // the dropdown value;
		int busiestShiftNumSlots;
		int busiestShiftPriority;
		List<Shift> shiftList;
		boolean isFullyAssigned;


		public Poll(Municipality municipality, String identifier, int busiestShiftNumSlots, int busiestShiftPriority)
		{
			this.municipality = municipality;
			this.identifier = identifier;
			this.busiestShiftNumSlots = busiestShiftNumSlots;
			this.busiestShiftPriority = busiestShiftPriority;

			/*
			 * TODO: construct `shiftList` using config shift weight info
			 */
		}


		class Shift
		{
			int shiftNumber;
			int totalSlots;
			int availableSlots;


			Shift(int shiftNumber, int totalSlots)
			{
				this.shiftNumber = shiftNumber;
				this.totalSlots = totalSlots;
				this.availableSlots = totalSlots;
			}


			int getAvailableSlots()
			{
				int ret = 0;

				// TODO: ??

				return (ret);
			}


			DateTime getEndTime()
			{
				DateTime ret = null;

				// TODO: ??

				return (ret);
			}


			int getPriority()
			{
				int ret = 0;

				// TODO: ??

				return (ret);
			}


			DateTime getStartTime()
			{
				DateTime ret = null;

				// TODO: ??

				return (ret);
			}


			int getTotalSlots()
			{
				int ret = 0;

				// TODO: ??

				return (ret);
			}


			boolean isFullyAssigned()
			{
				boolean ret = false;

				// TODO: ??

				return (ret);
			}


			int reserveSlot()
			{
				this.availableSlots--;

				return (this.availableSlots);
			}
		}


		boolean areShiftsAvailable(int... shifts)
		{
			boolean ret = false;

			// TODO: are all of the specified shifts available for this poll?

			return (ret);
		}


		List<Integer> availableShifts()
		{
			List<Integer> ret = null;

			// TODO: return all available shifts for this poll

			return (ret);
		}


		Municipality getMunicipality()
		{
			Municipality ret = null;

			// TODO: ??

			return (ret);
		}


		public boolean isFullyAssigned()
		{
			return (this.isFullyAssigned);
		}


		/**
		 * Returns {@code true} if this poll's zone is proximate to the specified zone. Two zones
		 * are proximate if {@link edmai.Configuration.ZoneConfig.isProximateZone(int, int)
		 * ZoneConfig.isProximateZone} returns {@code true}.
		 *
		 * @param zone
		 * @return
		 */
		public boolean isProximate(int zone)
		{
			boolean ret = false;

			// TODO: are all of the specified shifts available for this poll?

			return (ret);
		}


		public void reserveShifts(int... shifts)
		{
			/*
			 * TODO: reserve all specified shifts for this poll if all shifts have
			 * `this.isFullyAssigned = true`, set `this.isFullyAssigned = true` (will help
			 * performance)
			 */
		}
	}


	/**
	 * Returns a subset of {@code pollList} where each item has availability for all of the
	 * {@code pollWorker} shifts.
	 *
	 * @param pollList
	 * @param pollWorker
	 * @return
	 */
	private List<Poll> getAvailablePollsForExactShifts(List<Poll> pollList, PollWorker pollWorker)
	{
		List<Poll> ret = null;

		return (ret);
	}


	/**
	 * Returns a subset of {@code pollList} where each item has availability for one or more of the
	 * {@code pollWorker} shifts. If all of the {@code pollWorker} shifts are not available for any
	 * of the polls {@code pollList}, then {@code pollWorker} shifts are removed one at a time
	 * (lowest weight shifts are removed first) until all of those shifts are available.
	 *
	 * @param pollList
	 * @param pollWorker
	 * @return
	 */
	private List<Poll> getAvailablePollsForShifts(List<Poll> pollList, PollWorker pollWorker)
	{
		List<Poll> ret = new ArrayList<>();

		PollWorker pw = pollWorker;
		ret = this.getAvailablePollsForExactShifts(pollList, pw);

		while (ret.isEmpty())
		{
			pw = pw.removeLowestWeightShift();
			ret = this.getAvailablePollsForExactShifts(pollList, pw);
		}

		return (ret);
	}


	private Poll getHighestPriorityPoll(List<Poll> pollList)
	{
		Poll ret = null;

		// TODO: ??

		return (ret);
	}


	/**
	 * Returns a list of {@code Poll} from {@code pollList} where
	 * {@code poll.isProximateZone(pollWorker.getZone()) = true}
	 *
	 * @param pollList
	 * @param pollWorker
	 * @return
	 */
	private List<Poll> getProximatePolls(List<Poll> pollList, PollWorker pollWorker)
	{
		List<Poll> ret = null;

		// TODO: ??

		return (ret);
	}


	/**
	 * Returns a list of {@code Poll} from {@code pollList} containing just the polls for which
	 * {@code poll.isFullyAssigned = false}.
	 *
	 * @return
	 */
	private List<Poll> getUnassigned()
	{
		List<Poll> ret = null;

		// TODO: ??

		return (ret);
	}


	/**
	 * Get {@code Poll} list for polls that 1) are "proximate" (i.e. meet zone proximity criteria),
	 * 2) are not fully assigned, 3) have availability for all of the {@code PollWorker} slots
	 * (check that entire poll is not fully assigned first for performance). If none are available
	 * with all requested slots, then remove slots in ascending weight order and try again.
	 *
	 * @param pollWorker
	 * @return
	 */
	private List<Poll> getUnassignedProximateAvailablePolls(PollWorker pollWorker)
	{
		List<Poll> unassignedPolls = this.getUnassigned();
		List<Poll> unassignedProximatePolls = this.getProximatePolls(unassignedPolls, pollWorker);
		List<Poll> ret =
			this.getAvailablePollsForShifts(unassignedProximatePolls, pollWorker);

		return (ret);
	}


	public Set<Map.Entry<String, Poll>> entrySet()
	{
		return (this.pollMap.entrySet());
	}


	public Poll get(String pollId)
	{
		Poll ret = this.pollMap.get(pollId);
		return (ret);
	}


	public Set<String> keySet()
	{
		return (this.pollMap.keySet());
	}


	public Poll reservePoll(PollWorker pollWorker)
	{
		List<Poll> availableProximatePolls = this.getUnassignedProximateAvailablePolls(pollWorker);

		Poll ret = this.getHighestPriorityPoll(availableProximatePolls);

		return (ret);
	}
}

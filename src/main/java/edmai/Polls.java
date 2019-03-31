package edmai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

import edmai.Municipalities.Municipality;
import edmai.PollWorkers.PollWorker;
import edmai.Polls.Poll;

/**
 * Defines the collection of all {@link Poll} objects.
 * <p>
 * <b>IMPORTANT:</b> the contained polls are sorted by priority--first by the municipality priority
 * and then if the municipality priorities are the same, by the busiest shift priority. By sorting
 * the polls in this way, poll assignment is much more performant.
 *
 * @author Rob Oaks
 */
public class Polls implements Iterable<Poll>
{
	Set<Poll> pollSet;
	Configuration configuration;


	Polls(Range pollRange, Municipalities municipalities, Configuration configuration)
	{
		this.pollSet = new TreeSet<>();
		this.configuration = configuration;

		for (Range.Row row : pollRange)
		{
			Municipality muni = municipalities.get(row.getColumn(1).toString());
			String id = row.getColumn(5).toString();
			int busiestShiftNumSlots = (int)row.getColumn(6);
			int busiestShiftPriority = (int)row.getColumn(7);
			Poll poll = new Poll(muni, id, busiestShiftNumSlots, busiestShiftPriority);
			this.pollSet.add(poll);
		}
	}


	/**
	 * Defines a polling location and all of its attributes including:
	 * <ul>
	 * <li>An identifier--must uniquely identify the polling location. Currently, this is the value
	 * in the {@code Identifier} column in the {@code Assignment} sheet.
	 * <li>Its associated {@link Municipality}
	 * <li>The number of slots allocated for this poll during the busiest shift. The allocated slots
	 * for the other shifts can be calculated using
	 * {@link edmai.Configuration#calculateNumShiftSlots(int, int) calculateNumShiftSlots}, which
	 * uses the globally-defined <i>weight</i> of each shift.
	 * <li>The <i>priority</i> of the busiest shift. The priorities for the other shifts can be
	 * calculated using {@link edmai.Configuration#calculateShiftPriority(int, int)
	 * calculateShiftPriority}, which uses the globally-defined <i>weight</i> of each shift.
	 * <li>Shift list--the list of {@link Shift} objects associated with this poll
	 * </ul>
	 * <p>
	 * <b>IMPORTANT</b>: this class implements a comparator that first compares the priorities of
	 * the associated municipalities and, if those priorities are the same, compares the priorities
	 * of the busiest shift ({@code busiestShiftPriority}).
	 *
	 * @author Rob Oaks
	 */
	public class Poll implements Comparable<Poll>
	{
		private final Municipality municipality;
		private final String identifier; // the dropdown value;
		private final int busiestShiftNumSlots;
		private final int busiestShiftPriority;
		private final List<Shift> shiftList;
		private boolean isFullyAssigned;


		public Poll(Municipality municipality, String identifier, int busiestShiftNumSlots, int busiestShiftPriority)
		{
			this.municipality = municipality;
			this.identifier = identifier;
			this.busiestShiftNumSlots = busiestShiftNumSlots;
			this.busiestShiftPriority = busiestShiftPriority;
			this.shiftList = this.createShiftList(busiestShiftPriority);
		}


		/**
		 * Defines a {@link Poll} <i>shift</i>, which is a time period during which one or more poll
		 * workers work at the poll. Every poll has the same number of shifts, though their
		 * priorities and slots vary by poll. Each shift has the following attributes:
		 * <ul>
		 * <li>Shift number--shifts are numbered from {@code 0} to {@code num shifts - 1}
		 * <li>Priority--the priority of the shift relative to the other shifts at this poll
		 * </ul>
		 * Each shift starts with a specific number of <i>slots</i>--the desired number of poll
		 * workers for that shift at that poll. For each shift, slots are <i>reserved</i> until no
		 * more slots remain.
		 *
		 * @author Rob Oaks
		 */
		class Shift
		{
			int shiftNumber;
			int priority;
			int availableSlots;


			Shift(int shiftNumber, int priority)
			{
				this.shiftNumber = shiftNumber;
				this.priority = priority;
				int busiestShiftNumSlots = Poll.this.getBusiestShiftNumSlots();
				this.availableSlots =
					Polls.this.configuration.calculateNumShiftSlots(this.shiftNumber, busiestShiftNumSlots);
			}


			public int getAvailableSlots()
			{
				return (this.availableSlots);
			}


			public int getPriority()
			{
				return (this.priority);
			}


			/**
			 * Returns {@code true} if no more slots are available, and {@code false} otherwise.
			 *
			 * @return
			 */
			public boolean isFullyAssigned()
			{
				boolean ret = (this.availableSlots == 0);

				return (ret);
			}


			/**
			 * Attempts to <i>reserve</i> a slot. Returns {@code true} if a slot was successfully
			 * reserved and {@code false} otherwise.
			 *
			 * @return
			 */
			public boolean reserveSlot()
			{
				boolean ret = !this.isFullyAssigned();

				if (ret)
				{
					this.availableSlots--;
				}

				return (ret);
			}


			@Override
			public String toString()
			{
				return (String.valueOf(this.shiftNumber));
			}
		}


		private List<Shift> createShiftList(int busiestShiftPriority)
		{
			List<Shift> ret = new ArrayList<>();

			for (int i = 0; i < Polls.this.configuration.getNumShifts(); i++)
			{
				int shiftPriority = Polls.this.configuration.calculateShiftPriority(i, busiestShiftPriority);
				Shift shift = new Shift(i, shiftPriority);
				this.shiftList.add(shift);
			}

			return (ret);
		}


		private boolean meetsTravelFlexibilityRequirements(PollWorker pollWorker)
		{
			boolean ret = false;

			switch (pollWorker.getTravelFlexibility())
			{
			case ANYWHERE_IN_COUNTY:
				ret = true;
				break;

			case ANYWHERE_IN_MUNICIPALITY:
				ret = this.getMunicipality().equals(pollWorker.getMunicipality());
				break;

			case MY_POLL_ONLY:
				ret = this.equals(pollWorker.getHomePoll());
				break;
			}

			return (ret);
		}


		/**
		 * Returns {@code true} if all of the specified shifts are available (i.e. have unreserved
		 * slots) and {@code false} otherwise.
		 *
		 * @param shiftNumbers a list of shift numbers
		 * @return
		 */
		public boolean areSpecifiedShiftsAvailable(List<Integer> shiftNumbers)
		{
			boolean ret = true;

			for (int shiftNumber : shiftNumbers)
			{
				Shift shift = this.shiftList.get(shiftNumber);

				if (shift.isFullyAssigned())
				{
					ret = false;
					break;
				}
			}

			return (ret);
		}


		/**
		 * This comparator first compares the priorities of the associated municipalities and, if
		 * those priorities are the same, compares the priorities of the busiest shifts
		 * ({@code busiestShiftPriority}).
		 */
		@Override
		public int compareTo(Poll other)
		{
			int ret;

			if (other.getMunicipality().getPriority() > this.getMunicipality().getPriority())
			{
				ret = 1;
			}
			else if (other.getMunicipality().getPriority() < this.getMunicipality().getPriority())
			{
				ret = -1;
			}
			else
			{
				if (other.busiestShiftPriority > this.busiestShiftPriority)
				{
					ret = 1;
				}
				else if (other.busiestShiftPriority < this.busiestShiftPriority)
				{
					ret = -1;
				}
				else
				{
					ret = 0;
				}
			}

			return (ret);
		}


		@Override
		public boolean equals(final Object other)
		{
			if (!(other instanceof Poll))
				return false;
			Poll castOther = (Poll)other;
			return Objects.equal(this.identifier, castOther.identifier);
		}


		public int getBusiestShiftNumSlots()
		{
			return (this.busiestShiftNumSlots);
		}


		public int getBusiestShiftPriority()
		{
			return (this.busiestShiftPriority);
		}


		public String getIdentifier()
		{
			return (this.identifier);
		}


		public Municipality getMunicipality()
		{
			return (this.municipality);
		}


		public List<Shift> getShiftList()
		{
			return (this.shiftList);
		}


		/**
		 * Returns a string representation of the shift list.
		 *
		 * @return
		 */
		public String getShiftsString()
		{
			String ret = Joiner.on(',').join(this.shiftList);

			return (ret);
		}


		@Override
		public int hashCode()
		{
			return Objects.hashCode(this.identifier);
		}


		public boolean isFullyAssigned()
		{
			return (this.isFullyAssigned);
		}


		/**
		 * Returns {@code true} if this poll's <i>zone</i> (an attribute of its
		 * {@link Municipality}) is proximate to (i.e. nearby) the specified zone and {@code false}
		 * otherwise. Two zones are proximate if
		 * {@link edmai.Configuration#areZonesProximate(int, int) Configuration.areZonesProximate}
		 * returns {@code true}.
		 *
		 * @param zoneNumber
		 * @return
		 */
		public boolean isProximate(int zoneNumber)
		{
			boolean ret = Polls.this.configuration.areZonesProximate(this.municipality.getZone(), zoneNumber);

			return (ret);
		}


		/**
		 * Reserves a slot for all of the specified shifts.
		 *
		 * @param shiftNumbers
		 */
		public void reserveShifts(List<Integer> shiftNumbers)
		{
			boolean areAnyShiftsAvailable = false;

			for (int shiftNumber : shiftNumbers)
			{
				Shift shift = this.shiftList.get(shiftNumber);
				shift.reserveSlot();

				boolean shiftFullyAssigned = shift.isFullyAssigned();

				if (!shiftFullyAssigned)
				{
					areAnyShiftsAvailable = true;
				}
			}

			this.isFullyAssigned = !areAnyShiftsAvailable;
		}
	}


	/**
	 * For the specified {@link PollWorker}, returns the highest priority poll that meets all of the
	 * poll worker's requirements including:
	 * <ol>
	 * <li>Proximate to the poll worker's municipality
	 * <li>Meets the poll worker's travel flexibility needs
	 * <li>Has slots available for all of the poll worker's requested shifts
	 * </ol>
	 *
	 * @param pollWorker
	 * @return
	 */
	private Poll getHighestPriorityPollMeetingAllRequirements(PollWorker pollWorker)
	{
		Poll ret = null;

		/*
		 * Because polls are sorted in ascending priority order, we can simply traverse the polls in
		 * order until we find one that meets the proximity and availability criteria.
		 */
		for (Poll poll : this)
		{
			if (!poll.isFullyAssigned()
				&& poll.isProximate(pollWorker.getMunicipality().getZone())
				&& poll.meetsTravelFlexibilityRequirements(pollWorker)
				&& poll.areSpecifiedShiftsAvailable(pollWorker.getShiftNumbers()))
			{
				ret = poll;
				break;
			}
		}

		return (ret);
	}


	/**
	 * Returns the {@link Poll} corresponding to the specified poll identifier.
	 *
	 * @param pollIdentifier
	 * @return
	 */
	public Poll get(String pollIdentifier)
	{
		Poll ret = null;

		/*
		 * I realize this operation would be more efficient if the poll collection was a map instead
		 * of a set. Given the relative infrequency with which this method is called, I decided the
		 * map overhead was unjustified.
		 */
		for (Poll poll : this.pollSet)
		{
			if (poll.identifier.equals(pollIdentifier))
			{
				ret = poll;
				break;
			}
		}

		return (ret);
	}


	@Override
	public Iterator<Poll> iterator()
	{
		return (this.pollSet.iterator());
	}


	/**
	 * If possible, reserve a {@link Poll} for the specified {@link PollWorker}. This consists of
	 * reserving a slot in all of the poll worker's desired shifts.
	 * <p>
	 * The highest priority poll that is proximate (i.e. nearby) and available (i.e. not fully
	 * reserved) will be reserved.
	 *
	 * @param pollWorker
	 * @return
	 */
	public Poll reservePoll(PollWorker pollWorker)
	{
		Poll ret = this.getHighestPriorityPollMeetingAllRequirements(pollWorker);

		if (ret != null)
		{
			ret.reserveShifts(pollWorker.getShiftNumbers());
		}

		return (ret);
	}
}

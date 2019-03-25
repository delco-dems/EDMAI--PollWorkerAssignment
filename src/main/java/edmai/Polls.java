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
 * Defines the set of all {@link Poll} objects.
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


	Polls(NamedRange pollRange, Municipalities municipalities, Configuration configuration)
	{
		this.pollSet = new TreeSet<>();
		this.configuration = configuration;

		for (NamedRange.Row row : pollRange)
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
	 * Defines a polling location and all of its attributes.
	 * <p>
	 * <b>IMPORTANT</b>: this class implements a comparator that first compares the priorities of
	 * the associated municipalities and, if those priorities are the same, compares the priorities
	 * of the busiest shift ({@code busiestShiftPriority}).
	 *
	 * @author Rob Oaks
	 */
	public class Poll implements Comparable<Poll>
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

			this.shiftList = new ArrayList<>();

			for (int i = 0; i < Polls.this.configuration.getNumShifts(); i++)
			{
				float shiftWeight = Polls.this.configuration.getShiftWeight(i);
				int shiftPriority = Math.round(shiftWeight * busiestShiftPriority);
				Shift shift = new Shift(i, shiftPriority);
				this.shiftList.add(shift);
			}
		}


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


			int getAvailableSlots()
			{
				return (this.availableSlots);
			}


			int getPriority()
			{
				return (this.priority);
			}


			boolean isFullyAssigned()
			{
				boolean ret = (this.availableSlots == 0);

				return (ret);
			}


			boolean reserveSlot()
			{
				boolean ret = !this.isFullyAssigned();

				if (ret)
				{
					this.availableSlots--;
				}

				return (ret);
			}
		}


		boolean areAllShiftsAvailable(List<Integer> shiftNumbers)
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


		Municipality getMunicipality()
		{
			return (this.municipality);
		}


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


		public List<Shift> getShiftList()
		{
			return (this.shiftList);
		}


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
		 * Returns {@code true} if this poll's zoneNumber is proximate to the specified zoneNumber. Two zones
		 * are proximate if {@link edmai.Configuration.Zone.isProximateZone(int, int)
		 * Zone.isProximateZone} returns {@code true}.
		 *
		 * @param zoneNumber
		 * @return
		 */
		public boolean isProximate(int zone)
		{
			boolean ret = Polls.this.configuration.areZonesProximate(this.municipality.getZone(), zone);

			return (ret);
		}


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


	private Poll getHighestPriorityProximateAvailablePoll(PollWorker pollWorker)
	{
		Poll ret = null;

		for (Poll poll : this)
		{
			/*
			 * Polls will automatically be traversed in priority order
			 */
			if (!poll.isFullyAssigned()
				&& poll.isProximate(pollWorker.getMunicipality().getZone())
				&& poll.areAllShiftsAvailable(pollWorker.getShiftNumbers()))
			{
				ret = poll;
				break;
			}
		}

		return (ret);
	}


	public Poll get(String pollId)
	{
		Poll ret = null;

		for (Poll poll : this.pollSet)
		{
			if (poll.identifier.equals(pollId))
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


	public Poll reservePoll(PollWorker pollWorker)
	{
		Poll ret = this.getHighestPriorityProximateAvailablePoll(pollWorker);

		if (ret != null)
		{
			ret.reserveShifts(pollWorker.getShiftNumbers());
		}

		return (ret);
	}
}

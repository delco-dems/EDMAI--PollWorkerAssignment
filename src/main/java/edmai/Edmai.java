package edmai;

import edmai.PollWorkers.EdRole;
import edmai.PollWorkers.PollWorker;
import edmai.Polls.Poll;

public class Edmai
{
	private static Configuration configuration;
	private static Municipalities municipalities;
	private static Polls polls;
	private static PollWorkers pollWorkers;


	/**
	 * For the specified {@code Assignment} sheet row index, updates the {@code Poll--Calculated}
	 * and {@code Shifts--Calculated} columns in accordance with the specified {@link Poll}.
	 * <p>
	 * NOTE: this is a mock implementation.
	 *
	 * @param pollWorkerAssignmentRange
	 * @param index
	 * @param poll
	 */
	private static void updateAssignmentColumns(Range pollWorkerAssignmentRange, int index, Poll poll)
	{
		pollWorkerAssignmentRange.getRow(index).setColumn(0, poll.getIdentifier());
		pollWorkerAssignmentRange.getRow(index).setColumn(1, poll.getShiftsString());
	}


	public static void main(Object[] args)
	{
		/*
		 * TODO: replace the code below, which assumes that `GoogleRange` objects are passed on the
		 * command line, with the actual code to get the actual Google `Range` objects directly from
		 * the spreadsheet. For example, replace the line below with something like:
		 *
		 * var proximateZoneDistanceRange = new
		 * Range(SpreadsheetApp.getActive().getRange('ProximateZoneDistance'));
		 */
		Range proximateZoneDistanceRange = new Range((GoogleRange)args[0]);
		Range zoneConfigRange = new Range((GoogleRange)args[1]);
		Range shiftConfigRange = new Range((GoogleRange)args[2]);
		Range municipalityRange = new Range((GoogleRange)args[3]);
		Range pollRange = new Range((GoogleRange)args[4]);
		Range pollWorkerRange = new Range((GoogleRange)args[5]);
		Range pollWorkerAssignmentRange = new Range((GoogleRange)args[6]);

		/*
		 * Create the Configuration and collections
		 */
		Edmai.configuration =
			new Configuration(proximateZoneDistanceRange, zoneConfigRange, shiftConfigRange);
		Edmai.municipalities = new Municipalities(municipalityRange);
		Edmai.polls = new Polls(pollRange, Edmai.municipalities, Edmai.configuration);
		Edmai.pollWorkers = new PollWorkers(pollWorkerRange, Edmai.polls, Edmai.municipalities, Edmai.configuration);

		/*
		 * For each poll worker, reserve a poll and update the assignment columns in accordance with
		 * the reserved poll. For a give poll worker, if a poll cannot be reserved for the desired
		 * shifts, remove the lowest weight shift and try again until a poll can be reserved or no
		 * more polls are available.
		 */
		int index = 0;
		for (PollWorker pollWorker : Edmai.pollWorkers)
		{
			if (!pollWorker.getEdRoles().contains(EdRole.POLL_GREETER)
				&& !pollWorker.getEdRoles().contains(EdRole.POLL_WATCHER))
			{
				continue;
			}

			PollWorker pw = pollWorker;
			Poll poll = null;
			boolean pollWorkerHasExtraShifts = true;

			do
			{
				poll = Edmai.polls.reservePoll(pw);
				if (poll != null)
				{
					Edmai.updateAssignmentColumns(pollWorkerAssignmentRange, index, poll);
				}
				else
				{
					pollWorkerHasExtraShifts = pw.removeLowestWeightShift();
				}
			}
			while (pollWorkerHasExtraShifts && (poll == null));

			index++;
		}

		// Save all poll worker assignments back to the Google sheet
		pollWorkerAssignmentRange.save();
	}
}

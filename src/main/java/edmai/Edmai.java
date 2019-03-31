package edmai;

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
		 * For mocking purposes, load several `Range` objects from the command line. In reality,
		 * these named ranges will come directly from the spreadsheet.
		 */
		Range proximateZoneDistanceRange = (Range)args[0];
		Range zoneConfigRange = (Range)args[1];
		Range shiftConfigRange = (Range)args[2];
		Range municipalityRange = (Range)args[3];
		Range pollRange = (Range)args[4];
		Range pollWorkerRange = (Range)args[5];
		Range pollWorkerAssignmentRange = (Range)args[6];

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

package edmai;

import edmai.PollWorkers.PollWorker;
import edmai.Polls.Poll;

public class Edmai
{
	private static Configuration configuration;
	private static Municipalities municipalities;
	private static Polls polls;
	private static PollWorkers pollWorkers;


	public static void main(Object[] args)
	{
		NamedRange proximateZoneDistanceRange = (NamedRange)args[0];
		NamedRange zoneConfigRange = (NamedRange)args[1];
		NamedRange numShiftsRange = (NamedRange)args[2];
		NamedRange shiftConfigRange = (NamedRange)args[3];
		NamedRange municipalityRange = (NamedRange)args[4];
		NamedRange pollRange = (NamedRange)args[5];
		NamedRange pollWorkerRange = (NamedRange)args[6];

		Edmai.configuration =
			new Configuration(proximateZoneDistanceRange, zoneConfigRange, numShiftsRange, shiftConfigRange);
		Edmai.municipalities = new Municipalities(municipalityRange);
		Edmai.polls = new Polls(pollRange, Edmai.municipalities, Edmai.configuration);
		Edmai.pollWorkers = new PollWorkers(pollWorkerRange, Edmai.polls, Edmai.municipalities, Edmai.configuration);

		for (PollWorker pollWorker : Edmai.pollWorkers)
		{
			Poll poll = Edmai.polls.reservePoll(pollWorker);
			pollWorker.assign(poll);
		}
	}
}

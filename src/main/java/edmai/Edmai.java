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
		Edmai.configuration = new Configuration((NamedRange)args[0], (NamedRange)args[1]);
		Edmai.municipalities = new Municipalities((NamedRange)args[2]);
		Edmai.polls = new Polls((NamedRange)args[3], Edmai.municipalities);
		Edmai.pollWorkers = new PollWorkers((NamedRange)args[4], Edmai.polls, Edmai.municipalities);

		for (PollWorker pollWorker : Edmai.pollWorkers)
		{
			Poll poll = Edmai.polls.reservePoll(pollWorker);
			pollWorker.assign(poll);
		}
	}
}

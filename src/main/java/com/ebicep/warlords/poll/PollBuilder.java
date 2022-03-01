//package com.ebicep.warlords.poll;
//
//import com.ebicep.warlords.party.Party;
//import com.ebicep.warlords.party.Poll;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.function.Consumer;
//
//public class PollBuilder<T extends AbstractPoll> {
//
//    private final T poll;
//
//    public PollBuilder(T poll) {
//        this.poll = poll;
//    }
//
//    public PollBuilder setParty(Party party) {
//        poll.setParty(party);
//        return this;
//    }
//
//    public PollBuilder setQuestion(String question) {
//        poll.setQuestion(question);
//        return this;
//    }
//
//    public PollBuilder setInfiniteVotingTime(boolean infiniteVotingTime) {
//        poll.setInfiniteVotingTime(infiniteVotingTime);
//        return this;
//    }
//
//    public PollBuilder setOptions(List<String> options) {
//        poll.setOptions(options);
//        return this;
//    }
//
//    public PollBuilder setTimeLeft(int timeLeft) {
//        poll.setTimeLeft(timeLeft);
//        return this;
//    }
//
//    public PollBuilder setExcludedPlayers(List<UUID> excludedPlayers) {
//        poll.setExcludedPlayers(excludedPlayers);
//        return this;
//    }
//
//    public PollBuilder setRunnableAfterPollEnded(Consumer<Poll> runnableAfterPollEnded) {
//        poll.setOnPollEnd(runnableAfterPollEnded);
//        return this;
//    }
//
//    public AbstractPoll get() {
//        this.poll.init();
//        return this.poll;
//    }
//}

package hcmute.kltn.backend.service;

import hcmute.kltn.backend.dto.VoteStarDTO;

public interface VoteStarService {
    VoteStarDTO CUDVote(VoteStarDTO voteStarDTO);

    Float countAverageStar(String articleId);

    void addAverageStar(String articleId);
}

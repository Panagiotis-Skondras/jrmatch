package research.paper4.textmatch.util;


import research.paper4.textmatch.dto.CacheEntry;
import research.paper4.textmatch.dto.MatchResponse;
import research.paper4.textmatch.enums.DocumentPart;
import research.paper4.textmatch.enums.MatchAction;
import research.paper4.textmatch.impl.service.EmbeddingsAndMatchService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MatchUtils {

    public static List<MatchResponse> calculateSimilarity(Map<String, List<CacheEntry>> entriesToParseAndMatch, CacheEntry entryToMatch, String matchAction, boolean isResumeMatch) {

        List<MatchResponse> matchResult = new ArrayList<>();

        // case: mistral and nomic embeddings - keys maybe more than one
        // case: entriesToParseAndMatch either are jobPosts to resume or resumes to jobPost
        for (String entryKey : entriesToParseAndMatch.keySet()) {
            List<CacheEntry> cacheEntriesToParseAndMatch = entriesToParseAndMatch.get(entryKey);
            for (CacheEntry entry : cacheEntriesToParseAndMatch) {
                List<BigDecimal> gemmaScores = new ArrayList<>();
                List<BigDecimal> nomicScores = new ArrayList<>();
                MatchResponse matchResponse = new MatchResponse();
                matchResponse.setMatchAction(MatchAction.valueToEnum(matchAction));
                matchResponse.setResumeId(isResumeMatch ? entryToMatch.getEntryId() : entry.getEntryId());
                matchResponse.setResumeTitle(isResumeMatch ? entryToMatch.getFieldsInString().get(DocumentPart.TITLE.name()) : entry.getFieldsInString().get(DocumentPart.TITLE.name()));
                matchResponse.setJobPostId(isResumeMatch ? entry.getEntryId() : entryToMatch.getEntryId());
                matchResponse.setJobPostTitle(isResumeMatch ? entry.getFieldsInString().get(DocumentPart.TITLE.name()) : entryToMatch.getFieldsInString().get(DocumentPart.TITLE.name()));
                // get embeddings - mistral
                float[] jobPostTitle = isResumeMatch ? entry.getGemmaFieldsInFloat().get(DocumentPart.TITLE.name()) : entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.TITLE.name());
                float[] resumeTitle = isResumeMatch ? entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.TITLE.name()) : entry.getGemmaFieldsInFloat().get(DocumentPart.TITLE.name());
                double titleScore = EmbeddingsAndMatchService.cosineSimilarity(jobPostTitle, resumeTitle);
                BigDecimal tScore = new BigDecimal(titleScore).setScale(2, RoundingMode.HALF_UP);
                gemmaScores.add(tScore);
                matchResponse.setGemmaTitleScore(tScore.doubleValue());

                //nomic
                float[] jobPostTitleN = isResumeMatch ? entry.getNomicFieldsInFloat().get(DocumentPart.TITLE.name()) : entryToMatch.getNomicFieldsInFloat().get(DocumentPart.TITLE.name());
                float[] resumeTitleN = isResumeMatch ? entryToMatch.getNomicFieldsInFloat().get(DocumentPart.TITLE.name()) : entry.getNomicFieldsInFloat().get(DocumentPart.TITLE.name());
                double titleScoreN = EmbeddingsAndMatchService.cosineSimilarity(jobPostTitleN, resumeTitleN);
                BigDecimal tScoreN = new BigDecimal(titleScoreN).setScale(2, RoundingMode.HALF_UP);
                nomicScores.add(tScoreN);
                matchResponse.setNomicTitleScore(tScoreN.doubleValue());

                float[] jobPostDesc = isResumeMatch ? entry.getGemmaFieldsInFloat().get(DocumentPart.DESCRIPTION.name()) : entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.DESCRIPTION.name());
                float[] resumeDesc = isResumeMatch ? entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.DESCRIPTION.name()) : entry.getGemmaFieldsInFloat().get(DocumentPart.DESCRIPTION.name());
                double descScore = EmbeddingsAndMatchService.cosineSimilarity(jobPostDesc, resumeDesc);

                BigDecimal dScore = new BigDecimal(descScore).setScale(2, RoundingMode.HALF_UP);
                gemmaScores.add(dScore);
                matchResponse.setGemmaDescriptionScore(dScore.doubleValue());

                float[] jobPostDescN = isResumeMatch ? entry.getNomicFieldsInFloat().get(DocumentPart.DESCRIPTION.name()) : entryToMatch.getNomicFieldsInFloat().get(DocumentPart.DESCRIPTION.name());
                float[] resumeDescN = isResumeMatch ? entryToMatch.getNomicFieldsInFloat().get(DocumentPart.DESCRIPTION.name()) : entry.getNomicFieldsInFloat().get(DocumentPart.DESCRIPTION.name());
                double descScoreN = EmbeddingsAndMatchService.cosineSimilarity(jobPostDescN, resumeDescN);
                BigDecimal dScoreN = new BigDecimal(descScoreN).setScale(2, RoundingMode.HALF_UP);
                nomicScores.add(dScoreN);
                matchResponse.setNomicDescriptionScore(dScoreN.doubleValue());

                float[] jobPostResp = isResumeMatch ? entry.getGemmaFieldsInFloat().get(DocumentPart.RESPONSIBILITIES.name()) : entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.RESPONSIBILITIES.name());
                float[] resumeResp = isResumeMatch ? entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.RESPONSIBILITIES.name()) : entry.getGemmaFieldsInFloat().get(DocumentPart.RESPONSIBILITIES.name());
                double respScore = EmbeddingsAndMatchService.cosineSimilarity(jobPostResp, resumeResp);
                BigDecimal rScore = new BigDecimal(respScore).setScale(2, RoundingMode.HALF_UP);
                gemmaScores.add(rScore);
                matchResponse.setGemmaResponsibilitiesScore(rScore.doubleValue());

                float[] jobPostRespN = isResumeMatch ? entry.getNomicFieldsInFloat().get(DocumentPart.RESPONSIBILITIES.name()) : entryToMatch.getNomicFieldsInFloat().get(DocumentPart.RESPONSIBILITIES.name());
                float[] resumeRespN = isResumeMatch ? entryToMatch.getNomicFieldsInFloat().get(DocumentPart.RESPONSIBILITIES.name()) : entry.getNomicFieldsInFloat().get(DocumentPart.RESPONSIBILITIES.name());
                double respScoreN = EmbeddingsAndMatchService.cosineSimilarity(jobPostRespN, resumeRespN);
                BigDecimal rScoreN = new BigDecimal(respScoreN).setScale(2, RoundingMode.HALF_UP);
                nomicScores.add(rScoreN);
                matchResponse.setNomicResponsibilitiesScore(rScoreN.doubleValue());

                float[] jobPostEdu = isResumeMatch ? entry.getGemmaFieldsInFloat().get(DocumentPart.EDUCATION.name()) : entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.EDUCATION.name());
                float[] resumeEdu = isResumeMatch ? entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.EDUCATION.name()) : entry.getGemmaFieldsInFloat().get(DocumentPart.EDUCATION.name()) ;
                double eduScore = EmbeddingsAndMatchService.cosineSimilarity(jobPostEdu, resumeEdu);
                BigDecimal eScore = new BigDecimal(eduScore).setScale(2, RoundingMode.HALF_UP);
                gemmaScores.add(eScore);
                matchResponse.setGemmaEducationScore(eScore.doubleValue());

                float[] jobPostEduN = isResumeMatch ? entry.getNomicFieldsInFloat().get(DocumentPart.EDUCATION.name()) : entryToMatch.getNomicFieldsInFloat().get(DocumentPart.EDUCATION.name());
                float[] resumeEduN = isResumeMatch ? entryToMatch.getNomicFieldsInFloat().get(DocumentPart.EDUCATION.name()) : entry.getNomicFieldsInFloat().get(DocumentPart.EDUCATION.name());
                double eduScoreN = EmbeddingsAndMatchService.cosineSimilarity(jobPostEduN, resumeEduN);
                BigDecimal eScoreN = new BigDecimal(eduScoreN).setScale(2, RoundingMode.HALF_UP);
                nomicScores.add(eScoreN);
                matchResponse.setNomicEducationScore(eScoreN.doubleValue());

                float[] jobPostSkills = isResumeMatch ? entry.getGemmaFieldsInFloat().get(DocumentPart.SKILLS.name()) : entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.SKILLS.name());
                float[] resumeSkills = isResumeMatch ? entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.SKILLS.name()) : entry.getGemmaFieldsInFloat().get(DocumentPart.SKILLS.name());
                double skillsScore = EmbeddingsAndMatchService.cosineSimilarity(jobPostSkills, resumeSkills);
                BigDecimal sScore = new BigDecimal(skillsScore).setScale(2, RoundingMode.HALF_UP);
                gemmaScores.add(sScore);
                matchResponse.setGemmaSkillsScore(sScore.doubleValue());

                float[] jobPostSkillsN = isResumeMatch ? entry.getNomicFieldsInFloat().get(DocumentPart.SKILLS.name()) : entryToMatch.getNomicFieldsInFloat().get(DocumentPart.SKILLS.name());
                float[] resumeSkillsN = isResumeMatch ? entryToMatch.getNomicFieldsInFloat().get(DocumentPart.SKILLS.name()) : entry.getNomicFieldsInFloat().get(DocumentPart.SKILLS.name());
                double skillsScoreN = EmbeddingsAndMatchService.cosineSimilarity(jobPostSkillsN, resumeSkillsN);
                BigDecimal sScoreN = new BigDecimal(skillsScoreN).setScale(2, RoundingMode.HALF_UP);
                nomicScores.add(sScoreN);
                matchResponse.setNomicSkillsScore(sScoreN.doubleValue());

                float[] jobPostExp = isResumeMatch ? entry.getGemmaFieldsInFloat().get(DocumentPart.EXPERIENCE.name()) : entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.EXPERIENCE.name());
                float[] resumeExp = isResumeMatch ? entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.EXPERIENCE.name()) : entry.getGemmaFieldsInFloat().get(DocumentPart.EXPERIENCE.name());
                double expScore = EmbeddingsAndMatchService.cosineSimilarity(jobPostExp, resumeExp);
                BigDecimal exScore = new BigDecimal(expScore).setScale(2, RoundingMode.HALF_UP);
                gemmaScores.add(exScore);
                matchResponse.setGemmaExperienceScore(exScore.doubleValue());

                float[] jobPostExpN = isResumeMatch ? entry.getNomicFieldsInFloat().get(DocumentPart.EXPERIENCE.name()) : entryToMatch.getNomicFieldsInFloat().get(DocumentPart.EXPERIENCE.name());
                float[] resumeExpN = isResumeMatch ? entryToMatch.getNomicFieldsInFloat().get(DocumentPart.EXPERIENCE.name()) : entry.getNomicFieldsInFloat().get(DocumentPart.EXPERIENCE.name());
                double expScoreN = EmbeddingsAndMatchService.cosineSimilarity(jobPostExpN, resumeExpN);
                BigDecimal exScoreN = new BigDecimal(expScoreN).setScale(2, RoundingMode.HALF_UP);
                nomicScores.add(exScoreN);
                matchResponse.setNomicExperienceScore(exScoreN.doubleValue());

                float[] jobPostAddInfo = isResumeMatch ? entry.getGemmaFieldsInFloat().get(DocumentPart.ADDITIONAL_INFO.name()) : entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.ADDITIONAL_INFO.name());
                float[] resumeAddInfo = isResumeMatch? entryToMatch.getGemmaFieldsInFloat().get(DocumentPart.ADDITIONAL_INFO.name()) : entry.getGemmaFieldsInFloat().get(DocumentPart.ADDITIONAL_INFO.name());
                double addInfoScore = EmbeddingsAndMatchService.cosineSimilarity(jobPostAddInfo, resumeAddInfo);
                BigDecimal aScore = new BigDecimal(addInfoScore).setScale(2, RoundingMode.HALF_UP);
                //gemmaScores.add(aScore); not needed
                matchResponse.setGemmaAdditionalInfoScore(aScore.doubleValue());

                float[] jobPostAddInfoN = isResumeMatch ? entry.getNomicFieldsInFloat().get(DocumentPart.ADDITIONAL_INFO.name()) : entryToMatch.getNomicFieldsInFloat().get(DocumentPart.ADDITIONAL_INFO.name());
                float[] resumeAddInfoN = isResumeMatch? entryToMatch.getNomicFieldsInFloat().get(DocumentPart.ADDITIONAL_INFO.name()) : entry.getNomicFieldsInFloat().get(DocumentPart.ADDITIONAL_INFO.name());
                double addInfoScoreN = EmbeddingsAndMatchService.cosineSimilarity(jobPostAddInfoN, resumeAddInfoN);
                BigDecimal aScoreN = new BigDecimal(addInfoScoreN).setScale(2, RoundingMode.HALF_UP);
                //nomicScores.add(aScoreN); not needed
                matchResponse.setNomicAdditionalInfoScore(aScoreN.doubleValue());

                // todo : average for mistral must have weights - title description and education plays heavy role
                BigDecimal mistralSum = gemmaScores.stream()
                        .map(Objects::requireNonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                long count = gemmaScores.stream().filter(Objects::nonNull).count();
                BigDecimal mistralAverageScore = mistralSum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);

                BigDecimal nomicSum = nomicScores.stream()
                        .map(Objects::requireNonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                long countN = gemmaScores.stream().filter(Objects::nonNull).count();
                BigDecimal nomicAverageScore = nomicSum.divide(new BigDecimal(countN), 2, RoundingMode.HALF_UP);

                matchResponse.setGemmaOverallMatchScore(mistralAverageScore.doubleValue());
                matchResponse.setNomicOverallMatchScore(nomicAverageScore.doubleValue());
                matchResult.add(matchResponse);
            }
        }
        matchResult.sort((o1, o2) -> Double.compare(o2.getGemmaOverallMatchScore(), o1.getGemmaOverallMatchScore()));
        return matchResult;
    }

}

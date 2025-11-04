package research.paper4.textmatch.impl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import research.paper4.textmatch.dto.CacheEntry;
import research.paper4.textmatch.dto.CommonResponse;
import research.paper4.textmatch.dto.MatchRequest;
import research.paper4.textmatch.enums.MatchAction;

import javax.cache.Cache;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class CacheService {

    private Cache<String, List<CacheEntry>> resumesCache;
    private Cache<String, List<CacheEntry>> jobPostCache;

    @Autowired
    void setResumesCache(@Qualifier("resumesCache") Cache<String, List<CacheEntry>> resumesCache) {
        this.resumesCache = resumesCache;
    }

    @Autowired
    void setJobPostCache(@Qualifier("jobPostCache") Cache<String, List<CacheEntry>> jobPostCache) {
        this.jobPostCache = jobPostCache;
    }

    public void addResumeEntry(String title, CacheEntry entry) {

        if (resumesCache.containsKey(title)) {
            List<CacheEntry> entries = new ArrayList<>(resumesCache.get(title));
            entries.add(entry);
            List<CacheEntry> cacheEntries = new ArrayList<>(entries);
            resumesCache.getAndReplace(title, cacheEntries);
        } else {
            resumesCache.put(title, List.of(entry));
        }
    }

    public void addJobPostEntry(String title, CacheEntry entry) {

        if (jobPostCache.containsKey(title)) {
            List<CacheEntry> entries = new ArrayList<>(jobPostCache.get(title));
            entries.add(entry);
            jobPostCache.getAndReplace(title, entries);
        } else {
            jobPostCache.put(title, List.of(entry));
        }
    }

    public CommonResponse executeCacheAction(MatchRequest action) {

        if (action.getAction().equals(MatchAction.CLEAR_CACHE_JOB_POSTS.getAction())) {
            jobPostCache.clear();
            return new CommonResponse("Job posts cache cleared", null, null, null, null, null);
        }
        else if (action.getAction().equals(MatchAction.CLEAR_CACHE_RESUMES.getAction())) {
            resumesCache.clear();
            return new CommonResponse("Resumes cache cleared", null, null, null, null, null);
        }
        else if (action.getAction().equals(MatchAction.CLEAR_CACHE_ALL.getAction())) {
            resumesCache.clear();
            jobPostCache.clear();
            return new CommonResponse("All caches cleared", null, null, null, null, null);
        }
        else if(action.getAction().equals(MatchAction.FETCH_RESUME_CACHE_VALUES.getAction())){
            return new CommonResponse("Resumes", null, action.getResumeKeys(), fetchResumes(action.getResumeKeys()), null, null);
        }
        else if(action.getAction().equals(MatchAction.FETCH_JOB_POST_CACHE_VALUES.getAction())){
        return new CommonResponse("Job Posts", null, action.getJobPostKeys(), null, fetchJoPosts(action.getJobPostKeys()), null);
    }
        else if(action.getAction().equals(MatchAction.FETCH_ALL_CACHE_KEYS.getAction())){
            return new CommonResponse("All Keys Resumes - Job Posts", fetchAllCacheKeys(), null, null, null, null);
        }
        else {
            return new CommonResponse("No clear operation taken Invalid cache action", null, null, null, null, null);
        }

    }

    private List<Map<String, List<CacheEntry>>> fetchResumes(Set<String> resumeKeys){

        List<Map<String, List<CacheEntry>>> listElements = new ArrayList<>();
        for(String key : resumeKeys){
            listElements.add(Map.of(key, resumesCache.get(key)));
        }

        return listElements;
    }

    private List<Map<String, List<CacheEntry>>> fetchJoPosts(Set<String> jobPostKeys){

        List<Map<String, List<CacheEntry>>> listElements = new ArrayList<>();
        for(String key : jobPostKeys){
            listElements.add(Map.of(key, jobPostCache.get(key)));
        }

        return listElements;
    }

    private Map<String, List<String>> fetchAllCacheKeys(){

        List<String> resumeKeys = new ArrayList<>();
        resumesCache.iterator().forEachRemaining(e -> resumeKeys.add(e.getKey()));

        List<String> jobPostKeys = new ArrayList<>();
        jobPostCache.iterator().forEachRemaining(e -> jobPostKeys.add(e.getKey()));

        return Map.of("Resume", resumeKeys, "JobPost", jobPostKeys);
    }


}

package com.api.hitshot.domain.sites;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SiteServiceTest {
    @Test
    void registersNewSiteAtZeroWithoutCountingPreview() {
        SiteRepository repository = mock(SiteRepository.class);
        SiteService service = new SiteService(repository);
        String url = "https://new.example";
        ObjectId id = new ObjectId();
        when(repository.findByUrl(url)).thenReturn(Optional.empty());
        when(repository.save(any(SiteEntity.class))).thenAnswer(invocation -> {
            SiteEntity proposed = invocation.getArgument(0);
            assertEquals(0L, proposed.getVisitors());
            assertEquals(url, proposed.getUrl());
            return SiteEntity.builder().id(id).url(url).visitors(0L).build();
        });
        assertEquals(id, service.ensureSite(url));
        verify(repository).save(any(SiteEntity.class));
    }
}

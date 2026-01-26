package com.demo.app.controller;

import com.demo.app.service.HibernateTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/hibernate")
@RequiredArgsConstructor
public class HibernateTestController {

    private final HibernateTestService hibernateTestService;

    // ==================== DIRTY CHECKING ====================

    @PostMapping("/dirty-checking/without-save")
    public String dirtyCheckingWithoutSave() {
        hibernateTestService.dirtyCheckingWithoutSave();
        return "Check logs - UPDATE should happen WITHOUT calling save()";
    }

    @PostMapping("/dirty-checking/with-save")
    public String dirtyCheckingWithSave() {
        hibernateTestService.dirtyCheckingWithSave();
        return "Check logs - save() is redundant for managed entity";
    }

    @PostMapping("/dirty-checking/without-transaction")
    public String dirtyCheckingWithoutTransaction() {
        hibernateTestService.dirtyCheckingWithoutTransaction();
        return "Check logs - NO UPDATE because entity is detached";
    }

    @PostMapping("/dirty-checking/save-detached")
    public String saveDetachedEntity() {
        hibernateTestService.saveDetachedEntity();
        return "Check logs - MERGE happens for detached entity";
    }

    // ==================== CASCADE ====================

    @PostMapping("/cascade/insert-managed")
    public String cascadeInsertManaged() {
        hibernateTestService.cascadeInsertManaged();
        return "Check logs - MANAGED: dirty checking + cascade INSERT (no save)";
    }

    @PostMapping("/cascade/insert-detached")
    public String cascadeInsertDetached() {
        hibernateTestService.cascadeInsertDetached();
        return "Check logs - DETACHED: JOIN FETCH + save() + cascade INSERT";
    }

    @PostMapping("/cascade/lazy-exception")
    public String cascadeInsertLazyException() {
        hibernateTestService.cascadeInsertLazyException();
        return "This should throw LazyInitializationException";
    }

    @PostMapping("/cascade/insert-without-cascade")
    public String insertWithoutCascade() {
        hibernateTestService.insertWithoutCascade();
        return "Check logs - Seat INSERT directly via repository";
    }

    @PostMapping("/cascade/orphan-removal")
    public String orphanRemoval() {
        hibernateTestService.orphanRemoval();
        return "Check logs - Seat DELETE via orphanRemoval";
    }

    @PostMapping("/cascade/update")
    public String cascadeUpdate() {
        hibernateTestService.cascadeUpdate();
        return "Check logs - Flight + Seats UPDATE via dirty checking";
    }

    // ==================== HELPER ====================

    @PostMapping("/reset")
    public String reset() {
        hibernateTestService.resetFlightStatus();
        return "Flight and seats reset";
    }
}

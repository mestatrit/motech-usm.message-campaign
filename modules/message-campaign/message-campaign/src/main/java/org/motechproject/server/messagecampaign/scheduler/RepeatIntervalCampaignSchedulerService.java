package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.RepeatIntervalCampaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatIntervalCampaignMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.util.DateUtil.newDateTime;

@Component
public class RepeatIntervalCampaignSchedulerService extends CampaignSchedulerService<RepeatIntervalCampaignMessage, RepeatIntervalCampaign> {

    @Autowired
    public RepeatIntervalCampaignSchedulerService(MotechSchedulerService schedulerService, AllMessageCampaigns allMessageCampaigns) {
        super(schedulerService, allMessageCampaigns);
    }

    @Override
    protected void scheduleMessageJob(CampaignEnrollment enrollment, CampaignMessage m) {
        RepeatIntervalCampaign campaign = (RepeatIntervalCampaign) getAllMessageCampaigns().get(enrollment.getCampaignName());
        RepeatIntervalCampaignMessage message = (RepeatIntervalCampaignMessage) m;
        MotechEvent motechEvent = new MotechEvent(EventKeys.SEND_MESSAGE, jobParams(message.messageKey(), enrollment));
        DateTime start = newDateTime(enrollment.getReferenceDate(), deliverTimeFor(enrollment, message));
        DateTime end = start.plus(campaign.maxDuration());
        RepeatingSchedulableJob job = new RepeatingSchedulableJob()
            .setMotechEvent(motechEvent)
            .setStartTime(start.toDate())
            .setEndTime(end.toDate())
            .setRepeatIntervalInMilliSeconds(message.getRepeatIntervalInMillis())
            .setIgnorePastFiresAtStart(true)
            .setUseOriginalFireTimeAfterMisfire(true);
        getSchedulerService().safeScheduleRepeatingJob(job);
    }

    @Override
    public void stop(CampaignEnrollment enrollment) {
        RepeatIntervalCampaign campaign = (RepeatIntervalCampaign) getAllMessageCampaigns().get(enrollment.getCampaignName());
        for (RepeatIntervalCampaignMessage message : campaign.getMessages()) {
            getSchedulerService().safeUnscheduleRepeatingJob(EventKeys.SEND_MESSAGE, messageJobIdFor(message.messageKey(), enrollment.getExternalId(), enrollment.getCampaignName()));
        }
    }
}

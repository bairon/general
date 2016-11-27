package alsa.general;

import alsa.general.model.Riddle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * Created by alsa on 27.11.2016.
 */
@RestController
public class RiddleController {

    @Autowired
    Riddle riddle;

    @Autowired Dealer dealer;


    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/riddle", method = RequestMethod.POST)
    @ResponseBody
    public RiddleResponse riddle(@RequestBody RiddleRequest request) {
        System.out.println("Request " + request);
        if (request.number == -1) {
            return new RiddleResponse(riddle.getNext(), riddle.getLow(), riddle.getHigh(), riddle.getLast(), dealer.closestNumberAnnouncer(), dealer.gameTimeToStart());
        } else {
            if (request.result) {
                riddle.correct(request.number);
            } else {
                riddle.wrong(request.number);
            }
        }
        return null;
    }

}

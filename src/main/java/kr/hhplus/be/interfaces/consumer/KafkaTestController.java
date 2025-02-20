package kr.hhplus.be.interfaces.consumer;

import kr.hhplus.be.infrastructure.kafka.KafkaProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
public class KafkaTestController {

    private final KafkaProducer producer;

    public KafkaTestController(KafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String message) {
        producer.sendMessage("test-topic", message);
        return "Message sent: " + message;
    }
}


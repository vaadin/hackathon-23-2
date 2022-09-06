# hackathon-23-2 - Vesa

## What was done

Tested out the new Map component with browser geo location and adding the points of interests (= places) in Turku.

Found a nasty JRebel bug (and reported it) wrt. `@EventData` that is used in ComponentEvent<C>. If you define the EventData value incorrectly once, it won't be reloaded via JRebel but will require a server restart instead. E.g. if you have the following event defined in Flow:

```
@DomEvent("geo-location-received")
public static class GeoLocationReceivedEvent extends ComponentEvent<GeoLocation> {

    private final Double latitude;
    private final Double longitude;

    public GeoLocationReceivedEvent(GeoLocation source, boolean fromClient,
                                    @EventData("event.details.latitude") Double latitude,
                                    @EventData("event.details.longitude") Double longitude) {
        super(source, fromClient);
        this.latitude = latitude;
        this.longitude = longitude;
    }
```

you will be stuck with having `event.details` as the value no matter how many times you try to hotswap the code. The correct value would be `event.detail`.

![Screenshot 2022-09-06 at 8 41 29](https://user-images.githubusercontent.com/108755/188555650-46bbcaa0-3fb2-46d0-947b-a7ed18a90fbb.png)


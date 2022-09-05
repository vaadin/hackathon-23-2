Dear diary, these are my Hackathon findings


* Inconsistency between `Button.addClickListene` and `Map.addClickEventListener`. https://github.com/vaadin/flow-components/issues/3667
* MarkerFeature javadoc could mention how to add markers (i.e. through `map.getFeatureLayer().addFeature(marker)`)
* `location.setCoordinates(click.getCoordinate())` - why is one singular and the other is plural? https://github.com/vaadin/flow-components/issues/3668
* What's the point of the `MapBase` class other than confusing users when they look at getSource() of one of the events? 
* java.io.NotSerializableException: com.vaadin.flow.component.map.Assets$ImageAsset: https://github.com/vaadin/flow-components/issues/3672
* Error if adding same marker twice: https://github.com/vaadin/flow-components/issues/3671
* General Vaadin observations, not specific to the hackathon
  * `mvn` takes a long time to run with all the small Maven modules for all the components
  * The first time I started the app and logged in, I got a HTML document that only says "Ready". Reloading showed the actual application. Couldn't reproduce.
  * Annoying that you get 1 new tab every time you start the server - can it somehow detect that a similar tab is already open and reuse that one instead?
  * How do I create a custom field that has a regular field label, but the value isn't based on a popup button instead of inline field components. There's nothing in (obvious) javadocs that tells me I need to manually call setModelValue()
  * `Dialog.setModal` javadoc doesn't tell what the default is
  * Live reload doesn't work automatically if you've got the browser console open with "pause on exceptions" enabled since it always breaks on line 190 in StrategyHandler.js. Should be noted that the separate checkbox to also pause on caught exceptions wasn't enabled.

# Summary of my implementation

The application shows the points of interest surrounding the user's location.

As the user moves around the map, the app checks if the POI's surrounding the new location exceeds a certain threshold, for example, 10. 

If there are fewer POI's then the app fetches POI's from the overpass service for the current center of the map location.

Also, the zoom level is checked when the user moves around so that we do not fetch POI's when the user is zoomed out too far.

On clicking the feature marker, a popup is shown with the title and website link.

Adding a short video file for demo.



# hackathon-23-2
Repository for the 23.2 hackathon


## About the APP

### Repository Contents
This is a Vaadin 23.2 App that was downloaded from http://start.vaadin.com

It has 3 views, and there is no menu, so you need to type the URL for each page
  - /map - the map view - needs authentication
  - /places - master-detail for PLACE table - needs admin autentication
  - /tags - master-detail for TAG table - needs admin autentication

__NOTE__: for Hilla 1.2 users there is the [`main-hilla`](https://github.com/vaadin/hackathon-23-2/tree/main-hilla) branch instead.

### Data Generator
When the application starts, data generator creates:
  - Two users for login (user:user and admin:admin)
  - POIs for Turku (places and tags)
     - it uses a the [overpass api](https://wiki.openstreetmap.org/wiki/Overpass_API) querying a public [database](https://overpass-api.de)
     - if you want to fill the database with your city data change [`DataGenerator:CENTER`](https://github.com/vaadin/hackathon-23-2/blob/main/src/main/java/com/vaadin/example/sightseeing/data/generator/DataGenerator.java#L34) and [`DataGenerator:RATIO`](https://github.com/vaadin/hackathon-23-2/blob/main/src/main/java/com/vaadin/example/sightseeing/data/generator/DataGenerator.java#L35)

__NOTE__: Sometimes the query to the public service fails at first becausse a timeout (`OsmBadUserInputException: null`), re-run the app and it should work the second time because the server should have cached the results.

### Running the app

Just run `mvn` and open `https://localhost:8080` in your browser

__NOTE__: It requires JDK 17, but you can use 11 (see tips)

### Database
It uses spring JPA for managing an H2 database that persists in a local file, take a look to the [resources](https://github.com/vaadin/hackathon-23-2/blob/main/src/main/resources/application.properties#L12) file.

H2 has a web console that you can use to check data, just open `http://localhost:8080/h2-console` and type `jdbc:h2:./h2-app` in the URL field, leaving User and Password fields empty.

### Committing your work
Create a branch with your name and push to this repo, eg:

```
git clone https://github.com/vaadin/hackathon-23-2.git
cd hackathon-23-2
mvn
git checkout -b my_named_branch
git commit -m 'My changes'
git push
```

### Showing your work

Write a summary of the main characteristics or your app, it could be in the README.md of your branch, or if you prefer a slide. Screenshots in the summary would be nice.

Optionally you can deploy a live demo in heroku or any accessible server.

You might want to demo your app to the audience at the end of the session.

## The Sightseeing APP

ACME is a Travel Agency that wants to offer their passengers an App that facilitates tourism in cities along the route.

Sightseeing-App works in a way that once users are authenticated, they can access the map and media files.

The Map will show the points of interest around their current position.

Clicking on each place, they can see all the information available for it, and be able to download audio guides, docs, etc.

The app should be usable in small devices.

### Users

No registration page, only ACME admins would be able to register new users

### Places and Tags

Admins can add/remove/modify places in the cities.

Places have a set of tags which are properties describing the POI, and they should be editable.

### Media

Admins can add to each place media files like audio, pictures, documents and videos

### Stats

Optionally there could be a statistic module showing the most visited places, etc.

## Hackathon Challenges

During the Hackathon, you might achieve any of the following challenges

1. Display a marker in the map showing the actual user position, it should be updated when user moves
2. Display markers of all the POIs around the user position
3. Display POI info when Clicking on It
4. Show a clickable list of actions available for each point (urls, media files, etc)
5. Visiting the city works in mobile devices (for admin pages, desktop is enough)
6. Display a button to switch to the admin views
7. Be able to edit Places and their tags as well as disable/enable them
8. Be able to updload media files per place
9. User cannot zoom/move out of the current city

Bonuses
1. App works offline
2. There is a stat module
3. Admin can edit/add POIs by right-clicking on the map
5. Anything you come with, eg. a chat

## Hackaton Goals

The main reason of the hackathon is not the app itself, but be able to play with the new features in Vaadin and give feedback about them.

We will evaluate the following aspects

- Design of the application
- Use cases covered
- Number of new features (v23.2) used in the app
- Number of issues found (if possible reported as tickets)

### New Features in 23.2

Please check the full feature list in the [roadmap page](https://github.com/orgs/vaadin/projects/9), though here you have a summary

- Map (not experimental)
- MenuBar right aligned theme
- MultiSelect Combobox
- TextField pattern
- Vite as default
- License checker offline
- TestBench + Karibu
- Hilla Multimodule
- Autocomplete intelliJ for WC
- Java API for Lumo icons

## Tips

- Compute current position of the user
    You can use this [approach](https://github.com/mstahv/maptesting/blob/main/src/main/java/com/example/application/MainView.java#L44) by [Matti](https://github.com/mstahv/)
- Menu for Admins
    You can combine here 'Java API for Lumo icons' + 'MenuBar right aligned theme'
- Place editor
    You can add the new feature multiselect Combobox for selecting tags    
- You can change the JDK to 11 instead of 17
`mvn versions:set-property -Dproperty=java.version -DnewVersion=11 -q`
- For adjusting POI info returned by overpass service, adjust the [query](https://github.com/vaadin/hackathon-23-2/blob/main/src/main/java/com/vaadin/example/sightseeing/data/service/OverpassService.java#L27)
   - you can use this [frontend](https://overpass-turbo.eu/) for playing with querys
- To reset current database and re-run data generator, remove database file `rm -f ./h2-app.mv.db`
- Map documentation
  - [flow component](https://vaadin.com/docs/latest/components/map)
  - [web component](https://cdn-origin.vaadin.com/vaadin-web-components/23.2.0/index.html#/elements/vaadin-map)


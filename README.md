# Munchies

## Original Project Idea
This project is to create an Android application which the user could use as a guide to cook. The goal of the application is to simplify the meal choice by using only the ingredients found at home. The user will input the ingredients readily available, input some restrictions; such as low fat, high calories, etc, and the application will display recipes which use the available ingredients and restrictions.

### Project Description: 
Munchies is a food application that serves as a quick and convenient collection of recipes in your pocket. The user is able to register in the application so that data such as their favorite recipes can be saved with their user id. The main function of the application is the recipe search feature in which a regularly updated collection of recipes can be sorted through using any keywords that a user might use. Each of the recipes can have a rating - and the average rating over the number of user ratings will be taken into account. Munchies also provides a way to find the nearest grocery stores from your location for a user’s convenience.

### Prerequisites:
* Android phone/emulator	
* Minimum SDK: API 23: Android 6.0 (Marshmallow)
* Target SDK: API 26: Android 8.0 (Oreo)

## Milestone 2 - 12/8/2017

### Updates to Project Idea & Project Description
Based on the API we used, we as a group have decided to search by **RECIPE NAME** instead of ingredients. In regards to finding the nearest grocery stores, unfortunately as of February 2017 Google Places API has deprecated the types "food" and "grocery_store_or_supermarket." To compensate, Munchies will simply find the nearest store via the type "store" instead. A FAQ page has also been created.

### Everyone:
* Added a splash page (a few minutes) 
* Added more parts to the Main Activity (the whole development process of the app)
* Polished the overall look of the application along with improving various functional parts of the app (the whole development process of the app)

### Christian Ovid:
* Added Ratings feature - implemented it into RecyclerView items and Recipe Detail activity (one night)
* Made sure that users had their own ratings to the Firebase Database (one night)
* Fixed Search Bar issues (a few minutes)
* Helped Alejandro associate users with their own favorite recipes in the Firebase Database (a few minutes)
* Added some data fields along with their corresponding setters and getters in the Recipe class (a few minutes).

### Marinela Sanchez:
* Added more features to the Recipe Detail activity. Included calories, yield, Nutrient information, and health labels. Created “Get Recipe Button” that opens the phone’s browser and opens the web page with the recipe instructions. (3 hours)

### Alejandro Lemus:
* Favorites
	* Added ability to save favorites for a user (5 hours)

### Aenah Ramones:
* Finding Nearest store feature:
	* Implemented Google Maps API/Google Maps fragment in app (5 hours)
	* Implemented Permissions and Get Current Location (10 hours)
	* Implemented Google Places API and Google API Web Service; when “Find Nearest Store” is toggled, gets nearby stores (15 hours)
	* Implemented Google Navigation intent when marker clicked (1.5 hours)
	* Designed relevant XML files (1.5 hours)
* FAQ feature:
	* Implemented expandable list view as header and list view as child (5 hours)
	* Implemented web search and phone dial intents to specific answers (1 hour)
	* Implemented search bar that filtered through questions (2.5 hours)
	* Designed relevant XML files (1 hour)
* Custom Navigation Drawer feature; implemented navigation drawer menu to the app (3 hours)

## Milestone 1 - 11/17/2017

### Everyone:
* Created/Edited UI elements within the whole application (20 hours)

### Christian Ovid:
* Implemented adapter for RecyclerView (A whole day)
* Created layout file for a ViewHolder using CardView (a few minutes)
* Integrated the ViewHolder to RecyclerView (2 hours)

### Marinela Sanchez:
* Helped with research on how to make a json parser to get information off of an api (2 hours)
* Created the layout for the recipe detail activity (1 hour)
* Added ingredients to the recipe detail(30 min)
* Studying Edamam api to  learn how to read and use the api(1.5 hours)
* Troubleshooting issues with gradle (4 hours)

### Alejandro Lemus:
* Created the login/signup activities (4 hours)
* Integrated Firebase user authentication
* Integrated backend for api calls and JSON parsing (10 hours)

### Aenah Ramones:
* Created app logo (30 min)
* Created home page app background (1 hour)
* Fixing gradle issues (1 hour)

### Built with:
1. Android Studio
2. Firebase
3. Edamam API

### Sources:
	* The Android Documentation
		* RecyclerView
			* https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html
			* https://developer.android.com/training/material/lists-cards.html
		* Adding multiple lines to a text view
			* https://android--examples.blogspot.com/2015/01/textview-new-line-multiline-in-android.html
		* Scrollable Recipe ingredients
			* https://developer.android.com/reference/android/widget/ScrollView.html 
	* Firebase
		* https://firebase.google.com/
	* Edamam API
		* https://developer.edamam.com/

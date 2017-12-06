# Peregrine
Final Project Android App for INFO 448 - Android App Development - Fall 2017. Made with <3 by Jessica Libman, Benjamin Leeds, John Akers and Ishan Saksena. 

## App Description  
*The Problem*  
To safely pilot an airplane one must ensure a myriad of safety checks and information about the upcoming flight has been reviewed and found satisfactory. In the commercial airline world, pilots have maintenance and support teams to assist with these items whereas the general aviation pilot is left to contend with them on their own. The proposed app will give general aviation pilots a status dashboard view of the airplane they are flying to ensure legally required items are complied with prior to each and every flight. This dashboard will increase aviation safety by helping to minimize pilot error in the form of missed open maintenance and safety items.  

## Components of Functionality  
- Sending push notifications when a safety check or maintenance item needs to be completed (i.e. a reminder for a specific item that needs to be serviced every 24 months).  
- Providing a dynamic, detailed checklist that the pilot will be able to use for each pre-flight operation.  
- Assist in completing pre-flight operations that need to be conducted (checking the weather, route planning)   
- Providing access to new maintenance information about specific aircraft (not retrieving the actual information, as it is in PDF format,  but checking for new PDF updates)  

## Relevance to Mobile  
We expect pilots to use the app while they are planeside while conducting their pre-flight operations. Most pilots do not carry laptop computers but do carry tablets and/or phones. The app will also consume location data and an internet connection as we plan to display dynamic data based on weather and the model of the airplane. We also want the user to keep track of the hours they flew on a specific plane.  

## Manual  
The app helps you keep track of four different items:
 - Your Aircrafts
 - Inspection tasks for each aircraft. 
 - Pilot Physical and Medical Checks.
 - Airport specific flying restrictions.  
   
When you open the landing page, you might be asked to sign in or sign up with your email and password. If you sign in to multiple devices, your information will be synced across devices. You'll also be able to sign out from the side navigation bar.  

### Planes
Press the Planes button on the landing page to get here. You can see a list of planes you need to keep track of. These maybe shared between pilots, or a part of your private fleet. You can add new planes by pressing the plus button. You will be prompted to select a manufacturer and then one of the planes from them. For some planes, we will be able to automatically populate necessary inspection items. These vary widely from plane to plane so the user might sometimes have to add those herself. 

### Inspection tasks
Presents a list of upcoming tasks. You should be able to select one, and view requirements, descriptions and resources. You should also be able to press the + button and create a new inspection task. This will be synced across all devices. 

### Pilot Physical and Medical Checks
Pilots need to go through medical checkups regularly in order to legally fly. The user can add physical checks through the + button. 

### Upcoming flights
Here, we get your location and show you the weather for where you are. You can also enter the IATA code for the airports you're flying to and we'll pull data about flying restrictions from the FAA API.

### Home screen
Apart from buttons leading to the above screens, users are able to look at a upcoming tasks in order of due date. 

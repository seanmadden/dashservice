# dashservice

I can never remember to take my vitamins, so I made this service which will send me a [pushbullet](https://www.pushbullet.com/) notification 
if I haven't pressed my dash button by 9pm. I push the dash button when I've taken my medicine for the day. 
The count resets every morning at 5am.

I time the tasks with a cronjob:

    #Send a pushbullet notification if I haven't pushed my dash button yet
    0 21 * * * curl -l http://localhost:3000/haveITakenMyPills
    
    #Reset the button press at 5am
    0 05 * * * curl -l http://localhost:3000/reset
    

It's a really simple service, it doesn't store any data and only responds to three actions (button press, reset, notify).


# __Items File Creator For GE Notifier__

This application is a companion application for the [GE Notifier](https://github.com/st2092/ge-notifier). Search for the item then enter the price you are looking to buy and sell at. Once you have all your items set, generate the items file and run the GE Notifier python script.

**Note**: I am not responsible for any failed investments that occurred due to the usage of this project. It is only to help simplified the investment process.

## __How To Use__

Download the latest release from the releases tab.

Unzip the downloaded release and run the GE-Notifier-Items-Generator jar file by double clicking it.

You'll see the main interface pop up.

![Main Interface](/imgs/MainInterface.PNG)

When you are using this application for the first time you might need to update the database. It is recommended that you only do this if the items you are searching for are not available (i.e. new items added into the game).

![Updating Database](/imgs/UpdatingDatabase.gif)

Once the database update is completed, you can begin adding items. Press the add button to bring up the add item interface. Enter your item and then press select. Set the buy, sell, and margin prices and add it.

![Add Item Interface](/imgs/AddItemInterface.PNG)

Once you have all the items you want to track added, you can use the generate button to generate the items file for your item set. The items.json file will be in the same folder where this project is. Copy the items file over to where the GE Notifier script is and you're set!

![Generate Items File](/imgs/GenerateItemsFile.PNG)

## __What You Need__
To run this project, you will need to have the following installed:
* Java
* [MySQL](https://dev.mysql.com/downloads/)(Optional)
* [MySQL driver For Java](https://dev.mysql.com/downloads/connector/j/)(Optional)

For development, I would recommend Eclipse or Spring Tool.
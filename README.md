# calcumate_v3

GENERAL

Toolkit: Jetpack Compose

A tool that helps users identify the total amount of money they have, done via taking/selecting a photo of said money. 
Uses Google's ML Kit (text recognition, object detection) to identify currency within an image and totals the amount, displaying it back to the user.

Limitations:
- Currently only supports AU currency
- Custom models available can only detect 'Coins' no additional data on what currency value the coin is (i.e: 50 cents, 1 dollar...), requires additional input from user on # of coins they have for each coin type

WIP items:
- Allowing a user to select an existing photo to analyse
- UI cleanup
- Input validation on coins -> numeric only
- Bug fixes (theme, modal displays)

Improvements:
- Coroutines/multi thread processing
- Themes

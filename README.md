# FreeHat

Wear items in your helmet slot.

## Abstract

Players may put item in their helmet slot which do not usually go
there. They do so via command or inventory interaction. Items are
denied if they offer exploits. For example, you cannot put a
chestplate in your helmet slot because it would grant an unusually
high armor boost.

## Commands

- `/hat` Put the item in your hand slot in your helmet slot.

## Permissions

- `freehat.hat` Use the `/hat` command.
- `freehat.click` Click in your inventory to wear an item on your head.

## Rationale

This plugin attempts to accomplish the following guidelines.

- Print **feedback** in chat if any kind of action was taken.
- Do **nothing** if the item goes in the helmet slot naturally (helmets, skulls, pumpkins, etc).
- Print reject reasons if the command was used.
- Reject silently if a click was used.
- Avert **exploits** such as putting a chestplate in your helmet slot.
- Reject stacks of items.

## Planned Features

- With item stacks, intelligently grab one item and place it in the helmet slot.
- Respect right click appropriately, see above.
# Ender Dragon Tweaks

Small tweaks to the ender dragon fight:
- config to change the dragon's initial health, also adjusting the amount of regeneration it gets from ender crystals
- fix [MC-272431](https://bugs.mojang.com/browse/MC/issues/MC-272431)
- prevents fireballs from self hitting the dragon
- increases dragon breath duration from 30s to 45s
- prevents beds from damaging the ender dragon

## Default config
`enderdragontweaks.json`
```json
{
  "health": 400,
  "preventBedDamage": true
}
```

## Commands
`/enderdragontweaks reload` - Reloads the config from disk
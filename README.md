# Attribute-Base-Modifier (ABM)

The [MIT License](https://github.com/whyvo1/Attribute-Base-Modifier/blob/main/LICENSE) applies to all branches.

### Register Rules

Rules are registered by JSON files in the data pack.

The rule file should be in `data/<namespace>/abm_rules/<name>.json`.

If the rule is successfully registered, its ID will be `<namespace>:<name>`.

Like other data pack contents, all rules will be reloaded when the server reloads data. Specifically, when reload the world or use `reload` command.

JSON format:

- `<root>` ***Compound{}***
  - `include` ***Compound{}***  A include filter. Only the type specified here will be included. **Optional**, all type of mobs will be included if omitted. 
    - `type` ***String* | *List[]***  List the mobs that need to be included.
      - ***String***  either an ID or a tag(starts with `#`). *e.g*. `minecraft:zombie`, `#minecraft:undead`.
    - `spawn_group` ***String***  This means all mobs of this spawn group is included. Must be one of `monster`, `creature`, `ambient`, `water_creature`, `underground_water_creature`, `water_ambient`, `misc`, or `axolotls`. Has no effect when coexists with `type`.
  - `exclude` ***Compound{}***  List the mobs that need to be excluded. The format is the same as `include`. **Optional**, no mobs will be excluded if omitted.
  - `modifies` ***List[]***  Defines the modification methods.
    - ***Compound{}***  Define an attribute and the modification methods. If the mob lacks default value of this attribute, this modification will be skipped.
      - `attribute` ***String***  ID of the attribute . *e.g*. `minecraft:max_health`.
      - `modifiers` ***List[]***  The modification methods. The base values are calculated sequentially in order.
        - ***Compound{}***  A modification method.
          - `type` ***String***  The type of modification(operator). Must be one of `set`, `add`, `multiply`.
          - `value` ***Number***  The value of this modification. Will be parsed as a double value.
  - `default_enable` ***Boolean***  Whether to enable this rule by default. See Debug Command for more information. **Optional**, defaults to `true`.



### Debug Command

```
abmrule <rule_id> [enable|disable]
```

Specifically, you can enable or disable a rule by this command.

**This command is for debugging.**

When the rule is reloaded, this state will be reset to its default value. Specifically. the `default_enable` defined in the rule's JSON file.

# Using Navigators

## TreeNavigators

We will use this example JSON for this section:

```json
{
  "a": 17,
  "b": 15,
  "c": {
    "a": false,
    "b": {
      "a": "stringy",
      "d": [2, 4, 9]
    }
  }
}
```

To get the first value that matches:

```scala
json !>> "a"
```

will return the following:

```json
17
```

Multiple operators can be chained.

```scala
json !>> "c" !>> "b" !>> "d"
```

will return the following:
```json
[2, 4, 9]
```

To get all matches for a selector:

```scala
json !:>> "a"
```

this will return the following:
```json
[17, false, "stringy"]
```

## DOMNavigators

These are an extension of `TreeNavigator` that adds support for ID and class searches.

For example:

```scala
document !:@>> "beans"
```

will search for all HTML elements with a class of "beans" (equivalent to the `.beans` CSS selector).

To search for ID, you can similarly run:

```scala
document !#>> "submit"
```

This will return the first HTML element with an ID of "submit" (equivalent to the `#submit` CSS selector).

## Symbology

Operators are intended to be used in infix notation, but can be used with standard dot notation:
```scala
// these two are functionally equivalent
document !#>> "beans" !:@>> "some-class"
document.!#>>("beans").!:@>>("some-class")
```


- `!`: If added, the query will return another Navigator if successful, or will throw an exception if it fails. If
  omitted, the query will return an `Option` containing the resultant Navigator.
- `:`: If added, the query will return a list of all successful matches (as Navigators). If omitted, it will only return
  the first matching Navigator.
- The next symbol is only available for `DOMNavigator`s. These will automatically apply the following selector types
  when used:
  - `#`: select by element ID
  - `@`: select by element class
- `>>`: This ends every navigator operator.
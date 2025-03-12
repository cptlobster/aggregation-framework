# aggregation-framework-kafka

Extension package for pushing data collected via Aggregation Framework to a [Kafka](https://kafka.apache.org/)
datastore. This essentially turns your Aggregation Framework consumer application into a Kafka producer.

## Dependencies

You will need to configure a Kafka cluster to push your data to, including adding a topic for your target data type. For
testing, you can run Kafka in Docker using the `apache/kafka` image.

## Extensions
- `KafkaDatastore`: Datastore implementation for pushing data to a Kafka topic.

## License
This program is licensed under the [GNU General Public License, version 3](../../LICENSE.md).

*This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
version.*<br />
*This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.*
<br />
*You should have received a copy of the GNU General Public License along with this program. If not, see
https://www.gnu.org/licenses/.*
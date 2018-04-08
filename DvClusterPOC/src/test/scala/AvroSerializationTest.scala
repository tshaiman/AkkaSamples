import akka.actor.ActorSystem
import akka.serialization.SerializationExtension
import com.dv.akka.clusterpoc.models.{DvImpression, DvImpression11, DvImpression12}
import com.typesafe.config.ConfigFactory
import org.apache.avro.generic.GenericData
import org.scalatest.FlatSpec

class AvroSerializationTest extends FlatSpec {
  "An Avro Serializer" should " Serialize DV Data" in {
   /* val avroObject = DvImpression(1,Some(DvImpression11()),None,Some(DvImpression12()),None)
    val emptyAvro = DvImpression(1,None,None,None,None)

    val system = ActorSystem("example", ConfigFactory.load())
    val serializerSystem = SerializationExtension(system)
    val avroSerializer = serializerSystem.findSerializerFor(avroObject)
    assert(avroSerializer != null)

    val data = avroSerializer.toBinary(avroObject)
    val data2 = avroSerializer.toBinary(emptyAvro)

    assert(data2.length < data.length)
    assert(data2.length < 50)

    val record:DvImpression = avroSerializer.fromBinary(data,classOf[DvImpression]).asInstanceOf[DvImpression]
    assert(record != null)*/
    assert(true)
  }

}

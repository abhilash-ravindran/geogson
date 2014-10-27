/*
 * Copyright 2013 Filippo De Luca - me@filippodeluca.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.filosganga.geogson.jts;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.Before;
import org.junit.Test;

import com.github.filosganga.geogson.gson.GeometryAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
/**
 * @author Filippo De Luca - fdeluca@expedia.com
 */
public class JtsAdapterFactoryTest {

    private static final GeometryFactory gf = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);

    private Gson toTest;

    @Before
    public void initToTest() {
        // use the defined static GeometryFactory (so the conversion will work)
        toTest = new GsonBuilder()
                .registerTypeAdapterFactory(new GeometryAdapterFactory())
                .registerTypeAdapterFactory(new JtsAdapterFactory(gf))
                .create();
    }

    /**
     * helper method for assertion: the {@link Geometry geometry itself} as well
     * as the {@link PrecisionModel} and SRID must be equal!
     * @param parsed
     *          the parsed data using Gson
     * @param source
     *          original data ({@link JTS Geometry})
     */
    private void assertGeometryEquals(Geometry parsed, Geometry source) {
        assertThat(parsed, equalTo(source));
        if (parsed != null && source != null) {
            assertThat(parsed.getPrecisionModel(), equalTo(source.getPrecisionModel()));
            assertThat(parsed.getSRID(), equalTo(source.getSRID()));
        }
    }

    @Test
    public void shouldHandlePoint() {

        Point source = gf.createPoint(new Coordinate(56.7, 83.6));

        Point parsed = toTest.fromJson(toTest.toJson(source), Point.class);

        assertGeometryEquals(parsed, source);
    }

    @Test
    public void shouldHandleMultiPoint() {

        MultiPoint source = gf.createMultiPoint(new Coordinate[]{new Coordinate(56.7, 83.6), new Coordinate(43.9, 5.8)});

        MultiPoint parsed = toTest.fromJson(toTest.toJson(source), MultiPoint.class);

        assertGeometryEquals(parsed, source);
    }

    @Test
    public void shouldHandleLineString() {

        LineString source = gf.createLineString(new Coordinate[]{new Coordinate(56.7, 83.6), new Coordinate(43.9, 5.8)});

        LineString parsed = toTest.fromJson(toTest.toJson(source), LineString.class);

        assertGeometryEquals(parsed, source);
    }

    @Test
    public void shouldHandleLinearRing() {

        LinearRing source = gf.createLinearRing(new Coordinate[]{new Coordinate(56.7, 83.6), new Coordinate(43.9, 5.8), new Coordinate(43.9, 10), new Coordinate(56.7, 83.6)});

        LinearRing parsed = toTest.fromJson(toTest.toJson(source), LinearRing.class);

        assertGeometryEquals(parsed, source);
    }


    @Test
    public void shouldHandlePolygon() {

        Polygon source = gf.createPolygon(
                gf.createLinearRing(new Coordinate[]{new Coordinate(56.7, 83.6), new Coordinate(43.9, 5.8), new Coordinate(43.9, 10), new Coordinate(56.7, 83.6)}),
                new LinearRing[]{
                    gf.createLinearRing(new Coordinate[]{new Coordinate(46.7, 73.6), new Coordinate(33.9, 5.8), new Coordinate(33.9, 9), new Coordinate(46.7, 73.6)})
                }
        );

        Polygon parsed = toTest.fromJson(toTest.toJson(source), Polygon.class);

        assertGeometryEquals(parsed, source);
    }


    @Test
    public void shouldHandleMultiPolygon() {

        MultiPolygon source = gf.createMultiPolygon(new Polygon[]{
                gf.createPolygon(
                        gf.createLinearRing(new Coordinate[]{new Coordinate(56.7, 83.6), new Coordinate(43.9, 5.8), new Coordinate(43.9, 10), new Coordinate(56.7, 83.6)}),
                        new LinearRing[]{
                                gf.createLinearRing(new Coordinate[]{new Coordinate(46.7, 73.6), new Coordinate(33.9, 5.8), new Coordinate(33.9, 9), new Coordinate(46.7, 73.6)})
                        }
                ),
                gf.createPolygon(
                        gf.createLinearRing(new Coordinate[]{new Coordinate(56.7, 83.6), new Coordinate(43.9, 5.8), new Coordinate(43.9, 10), new Coordinate(56.7, 83.6)}),
                        new LinearRing[]{
                                gf.createLinearRing(new Coordinate[]{new Coordinate(46.7, 73.6), new Coordinate(33.9, 5.8), new Coordinate(33.9, 9), new Coordinate(46.7, 73.6)})
                        }
                )
        });

        MultiPolygon parsed = toTest.fromJson(toTest.toJson(source), MultiPolygon.class);

        assertGeometryEquals(parsed, source);
    }

    @Test
    public void shouldReadMultiPolygon() {

        String json = "{\"coordinates\": [\n" +
                "                    [\n" +
                "                        [\n" +
                "                            [\n" +
                "                                1.275274,\n" +
                "                                52.065723\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                1.278033,\n" +
                "                                52.055747\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                1.288243,\n" +
                "                                52.054784\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                1.28686,\n" +
                "                                52.060539\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                1.282106,\n" +
                "                                52.064237\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                1.275274,\n" +
                "                                52.065723\n" +
                "                            ]\n" +
                "                        ]\n" +
                "                    ],\n" +
                "                    [\n" +
                "                        [\n" +
                "                            [\n" +
                "                                -0.50156,\n" +
                "                                51.63172\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.4990270341569025,\n" +
                "                                51.61637765176749\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.504265,\n" +
                "                                51.614442\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.504288,\n" +
                "                                51.606141\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.4985,\n" +
                "                                51.600635\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.499182,\n" +
                "                                51.595587\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.506648,\n" +
                "                                51.597257\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.516513,\n" +
                "                                51.603808\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.521724,\n" +
                "                                51.602961\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.52087,\n" +
                "                                51.592767\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.529772,\n" +
                "                                51.587385\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.532807,\n" +
                "                                51.578688\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.528496,\n" +
                "                                51.575861\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.531857,\n" +
                "                                51.565421\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.526126,\n" +
                "                                51.557201\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.512361,\n" +
                "                                51.553699\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.502468,\n" +
                "                                51.555323\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.493115289193991,\n" +
                "                                51.543350136770684\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.4942131712846886,\n" +
                "                                51.542172173868664\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.499145,\n" +
                "                                51.540898\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.498872,\n" +
                "                                51.535695\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.4958056819617701,\n" +
                "                                51.533927207797035\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.49174,\n" +
                "                                51.49888\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.51221,\n" +
                "                                51.46822\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.4529413248920495,\n" +
                "                                51.45346223189963\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.454825,\n" +
                "                                51.451894\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.461279,\n" +
                "                                51.44864\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.458464,\n" +
                "                                51.439559\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.454115,\n" +
                "                                51.438454\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.446258,\n" +
                "                                51.441105\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.438085,\n" +
                "                                51.43797\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.43837,\n" +
                "                                51.4346\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.437968,\n" +
                "                                51.433244\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.423386,\n" +
                "                                51.430199\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.420431,\n" +
                "                                51.4316\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.409579,\n" +
                "                                51.429704\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.407916,\n" +
                "                                51.422635\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.393332600954472,\n" +
                "                                51.42202402517712\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.393377,\n" +
                "                                51.421977\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.3897641741441074,\n" +
                "                                51.419871910398\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.39065,\n" +
                "                                51.40973\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.3823211800901068,\n" +
                "                                51.41036476652082\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.382317,\n" +
                "                                51.410359\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.37759,\n" +
                "                                51.410141\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.375435,\n" +
                "                                51.408624\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.365355,\n" +
                "                                51.411179\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.3588428101734087,\n" +
                "                                51.41043641955785\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.32695,\n" +
                "                                51.39136\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.3221709329815611,\n" +
                "                                51.392431497168815\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.322103,\n" +
                "                                51.392316\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.322108,\n" +
                "                                51.376705\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.323606,\n" +
                "                                51.372724\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.330636,\n" +
                "                                51.371835\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.325032,\n" +
                "                                51.364829\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.320741,\n" +
                "                                51.366392\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.318946,\n" +
                "                                51.358694\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.325497,\n" +
                "                                51.355451\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.325502,\n" +
                "                                51.350044\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.331828,\n" +
                "                                51.347643\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.329534,\n" +
                "                                51.342706\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.336482,\n" +
                "                                51.333201\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.308857,\n" +
                "                                51.330243\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.301436,\n" +
                "                                51.342928\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.301075,\n" +
                "                                51.349334\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.286997,\n" +
                "                                51.35202\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.288672,\n" +
                "                                51.36216\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.286248,\n" +
                "                                51.363427\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.281392,\n" +
                "                                51.371595\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.278302,\n" +
                "                                51.372131\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.2745,\n" +
                "                                51.376525\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.2671868916617864,\n" +
                "                                51.37978215855149\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.274472,\n" +
                "                                51.376519\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.278273,\n" +
                "                                51.372124\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.269451,\n" +
                "                                51.371738\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.266678,\n" +
                "                                51.369006\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.263261,\n" +
                "                                51.369347\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.263152,\n" +
                "                                51.370041\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.25942,\n" +
                "                                51.369713\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.252875,\n" +
                "                                51.371096\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.249526,\n" +
                "                                51.369696\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.249513,\n" +
                "                                51.368347\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.242954,\n" +
                "                                51.368012\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.242254,\n" +
                "                                51.36765\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.239839,\n" +
                "                                51.36833\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.238522,\n" +
                "                                51.368012\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.235857,\n" +
                "                                51.366267\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.23221,\n" +
                "                                51.365888\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.232334,\n" +
                "                                51.367602\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.230887547333058,\n" +
                "                                51.36890679253446\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.232329,\n" +
                "                                51.367585\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.230967,\n" +
                "                                51.351199\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.236357,\n" +
                "                                51.34717\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.23154,\n" +
                "                                51.345025\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.227589,\n" +
                "                                51.337204\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.218252,\n" +
                "                                51.339315\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.211609,\n" +
                "                                51.333359\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.207277,\n" +
                "                                51.335644\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.198421,\n" +
                "                                51.335767\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.194765,\n" +
                "                                51.332157\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.187931,\n" +
                "                                51.331675\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.185858,\n" +
                "                                51.336754\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.18759,\n" +
                "                                51.338499\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.187221,\n" +
                "                                51.340835\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.185753,\n" +
                "                                51.343901\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.182883,\n" +
                "                                51.343966\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.182716,\n" +
                "                                51.347132\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.179044,\n" +
                "                                51.348095\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.177141,\n" +
                "                                51.34941\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.178977,\n" +
                "                                51.350827\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.173436,\n" +
                "                                51.357258\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.177641,\n" +
                "                                51.35872\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.175939,\n" +
                "                                51.359823\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.177174,\n" +
                "                                51.361906\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.1722,\n" +
                "                                51.364911\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.174237,\n" +
                "                                51.366185\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.174604,\n" +
                "                                51.369269\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.171799,\n" +
                "                                51.369276\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.171751,\n" +
                "                                51.371048\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.175891,\n" +
                "                                51.372738\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.1760172949950749,\n" +
                "                                51.37379045829229\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.175968,\n" +
                "                                51.373787\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.17577,\n" +
                "                                51.372742\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.171543,\n" +
                "                                51.371014\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.171542,\n" +
                "                                51.369129\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.174454,\n" +
                "                                51.369129\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.174141,\n" +
                "                                51.366274\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.171905,\n" +
                "                                51.364877\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.176947,\n" +
                "                                51.361988\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.175699,\n" +
                "                                51.359778\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.17653,\n" +
                "                                51.359228\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.174711,\n" +
                "                                51.358187\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.170344,\n" +
                "                                51.357213\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.166289,\n" +
                "                                51.357248\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.156828,\n" +
                "                                51.359037\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.155944,\n" +
                "                                51.360109\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.158127,\n" +
                "                                51.362964\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.155137,\n" +
                "                                51.366277\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.158517,\n" +
                "                                51.367804\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.1578414599486653,\n" +
                "                                51.36849438708543\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.158432,\n" +
                "                                51.367814\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.154934,\n" +
                "                                51.366386\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.157947,\n" +
                "                                51.362923\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.155776,\n" +
                "                                51.360212\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.157102,\n" +
                "                                51.358477\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.155895,\n" +
                "                                51.355919\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.159269,\n" +
                "                                51.352682\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.155893,\n" +
                "                                51.351479\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.155771,\n" +
                "                                51.347187\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.159627,\n" +
                "                                51.344397\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.157337,\n" +
                "                                51.343949\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.154563,\n" +
                "                                51.341012\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.1502328485970031,\n" +
                "                                51.34049106452354\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.15027,\n" +
                "                                51.340466\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.151599,\n" +
                "                                51.338465\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.148762,\n" +
                "                                51.33675\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.148719,\n" +
                "                                51.335333\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.151571,\n" +
                "                                51.332621\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.146385,\n" +
                "                                51.328585\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.138752,\n" +
                "                                51.33161\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.139064,\n" +
                "                                51.32949\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.136098,\n" +
                "                                51.329169\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.130445,\n" +
                "                                51.326402\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.127064,\n" +
                "                                51.32644\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.128422,\n" +
                "                                51.323295\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.130364,\n" +
                "                                51.322297\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.130065,\n" +
                "                                51.321392\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.125544,\n" +
                "                                51.319866\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.120969,\n" +
                "                                51.317759\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.119589,\n" +
                "                                51.313702\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.104134,\n" +
                "                                51.305475\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.10415,\n" +
                "                                51.302739\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.099341,\n" +
                "                                51.302367\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.100404,\n" +
                "                                51.300318\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.100319,\n" +
                "                                51.299229\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.095595,\n" +
                "                                51.298559\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.094147,\n" +
                "                                51.299276\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.094233,\n" +
                "                                51.300263\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.091424,\n" +
                "                                51.301329\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.091935,\n" +
                "                                51.304341\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.092786,\n" +
                "                                51.305082\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.090999,\n" +
                "                                51.306547\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.090375,\n" +
                "                                51.308681\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.091482,\n" +
                "                                51.310946\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.090375,\n" +
                "                                51.312278\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.090163,\n" +
                "                                51.315068\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.087992,\n" +
                "                                51.315764\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.083649,\n" +
                "                                51.315365\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.081137,\n" +
                "                                51.315601\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.080626,\n" +
                "                                51.31571\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.076941,\n" +
                "                                51.317414\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.0778757333312104,\n" +
                "                                51.319884010382154\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.077862,\n" +
                "                                51.31987\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.070761,\n" +
                "                                51.320173\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.067337,\n" +
                "                                51.317911\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.065715,\n" +
                "                                51.31952\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.063588,\n" +
                "                                51.319097\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.061004,\n" +
                "                                51.321649\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.052166,\n" +
                "                                51.32365\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.052284,\n" +
                "                                51.326625\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.042902,\n" +
                "                                51.33223\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.029187,\n" +
                "                                51.333271\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.018947,\n" +
                "                                51.326177\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.005808,\n" +
                "                                51.330282\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.003422,\n" +
                "                                51.330144\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.006873,\n" +
                "                                51.338068\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.00479,\n" +
                "                                51.357455\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.011135,\n" +
                "                                51.359538\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.01515,\n" +
                "                                51.365037\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.024403,\n" +
                "                                51.367155\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.024679,\n" +
                "                                51.371711\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.026452,\n" +
                "                                51.376991\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.0275974191631737,\n" +
                "                                51.377693824280804\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.024325,\n" +
                "                                51.38339\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.022065,\n" +
                "                                51.382462\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.000144,\n" +
                "                                51.390746\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.006897,\n" +
                "                                51.398786\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.011807,\n" +
                "                                51.398694\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.011721,\n" +
                "                                51.400868\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.008596,\n" +
                "                                51.401327\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.009547,\n" +
                "                                51.404518\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.0095417563059286,\n" +
                "                                51.40451977545228\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.008495,\n" +
                "                                51.401281\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.011606,\n" +
                "                                51.400879\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.01172,\n" +
                "                                51.398722\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.006854,\n" +
                "                                51.398867\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.002332,\n" +
                "                                51.388519\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.005556,\n" +
                "                                51.385984\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.002044,\n" +
                "                                51.378234\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.002044,\n" +
                "                                51.376292\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.003799,\n" +
                "                                51.375565\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.003425,\n" +
                "                                51.370201\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.017568,\n" +
                "                                51.367193\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.017508,\n" +
                "                                51.361451\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.004258,\n" +
                "                                51.357113\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.007118,\n" +
                "                                51.338202\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.017584,\n" +
                "                                51.333893\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.024486,\n" +
                "                                51.333758\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.033282,\n" +
                "                                51.330208\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.048969,\n" +
                "                                51.317876\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.06772,\n" +
                "                                51.323911\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.070992,\n" +
                "                                51.339219\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.082703,\n" +
                "                                51.342254\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.090424,\n" +
                "                                51.341573\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.093943,\n" +
                "                                51.325701\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.104552,\n" +
                "                                51.31916\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.116796,\n" +
                "                                51.329678\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.117039,\n" +
                "                                51.346073\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.13236,\n" +
                "                                51.347771\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.152948,\n" +
                "                                51.345381\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.166987,\n" +
                "                                51.353997\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.177795,\n" +
                "                                51.355479\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.170951,\n" +
                "                                51.367758\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.149537,\n" +
                "                                51.375357\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.138885,\n" +
                "                                51.384117\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.142069,\n" +
                "                                51.389839\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.136059,\n" +
                "                                51.392334\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.133607,\n" +
                "                                51.395542\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.1435245767062683,\n" +
                "                                51.40476480960688\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.1437,\n" +
                "                                51.404716\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.152215,\n" +
                "                                51.406152\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.153206,\n" +
                "                                51.409593\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.150247,\n" +
                "                                51.416366\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.151918,\n" +
                "                                51.420849\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.161057,\n" +
                "                                51.42267\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.169948,\n" +
                "                                51.423337\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.169715,\n" +
                "                                51.426309\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.173545,\n" +
                "                                51.42578\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.178607,\n" +
                "                                51.427064\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.178461,\n" +
                "                                51.437628\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.168616,\n" +
                "                                51.440935\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.168194,\n" +
                "                                51.451453\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.164103,\n" +
                "                                51.452307\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.164458,\n" +
                "                                51.45494\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.170445,\n" +
                "                                51.455936\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.171283,\n" +
                "                                51.460319\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.178971,\n" +
                "                                51.461226\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.1763615061366737,\n" +
                "                                51.468735543451125\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.176488,\n" +
                "                                51.468733\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.178381,\n" +
                "                                51.463279\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.180988,\n" +
                "                                51.464529\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.183238,\n" +
                "                                51.462996\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.186451,\n" +
                "                                51.462392\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.19084,\n" +
                "                                51.463597\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.196409,\n" +
                "                                51.461531\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.206474,\n" +
                "                                51.461866\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.2099,\n" +
                "                                51.464263\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.217932,\n" +
                "                                51.4676\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.228109,\n" +
                "                                51.476602\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.216757,\n" +
                "                                51.484412\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.197451,\n" +
                "                                51.483746\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.1954261543838906,\n" +
                "                                51.48401919239069\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.1955383743934173,\n" +
                "                                51.4840717969216\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.19753,\n" +
                "                                51.483814\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.1980315475411072,\n" +
                "                                51.48524050297018\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.26589,\n" +
                "                                51.51705\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.26739,\n" +
                "                                51.53083\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.33931,\n" +
                "                                51.53968\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.31619,\n" +
                "                                51.56727\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.29254,\n" +
                "                                51.56606\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.26989,\n" +
                "                                51.60895\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.2293581070573526,\n" +
                "                                51.63052007885391\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.227666,\n" +
                "                                51.632686\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.217065,\n" +
                "                                51.634214\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.2133245727169526,\n" +
                "                                51.63182134450146\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.2028549410688581,\n" +
                "                                51.630897341983065\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.199763,\n" +
                "                                51.635947\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.235383,\n" +
                "                                51.647249\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.236185,\n" +
                "                                51.656673\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.224727,\n" +
                "                                51.670272\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.194931,\n" +
                "                                51.660011\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.176248,\n" +
                "                                51.685389\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.159976,\n" +
                "                                51.668761\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.100895,\n" +
                "                                51.655441\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.098655,\n" +
                "                                51.644372\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.1319696449196759,\n" +
                "                                51.62450239064463\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.07141,\n" +
                "                                51.60892\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.026141766445725,\n" +
                "                                51.63167710154554\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.03267,\n" +
                "                                51.63985\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.0244329555296222,\n" +
                "                                51.64137721641118\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.024989,\n" +
                "                                51.643801\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.02498522370635,\n" +
                "                                51.6438046198164\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.024986,\n" +
                "                                51.643808\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.006359,\n" +
                "                                51.673854\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.0070725793835463,\n" +
                "                                51.672328357939044\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.0191248752221899,\n" +
                "                                51.64657497102001\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.006475,\n" +
                "                                51.673935\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.020991,\n" +
                "                                51.677278\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.026397,\n" +
                "                                51.680451\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.051634,\n" +
                "                                51.680334\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.103018,\n" +
                "                                51.691399\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.115973,\n" +
                "                                51.696962\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.129616,\n" +
                "                                51.693655\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.128936,\n" +
                "                                51.688975\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.149596,\n" +
                "                                51.688032\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.154633,\n" +
                "                                51.682103\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.175842,\n" +
                "                                51.677818\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.186051,\n" +
                "                                51.683871\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.220133,\n" +
                "                                51.684718\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.237503,\n" +
                "                                51.681446\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.237575,\n" +
                "                                51.673244\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.241154,\n" +
                "                                51.657004\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.247012,\n" +
                "                                51.650482\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.250749,\n" +
                "                                51.643006\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.257895,\n" +
                "                                51.642022\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.25799,\n" +
                "                                51.638407\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.252562,\n" +
                "                                51.641005\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.248826,\n" +
                "                                51.635418\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.196281,\n" +
                "                                51.640574\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.1960909948871499,\n" +
                "                                51.64055410034207\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.248897,\n" +
                "                                51.635326\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.252633,\n" +
                "                                51.640807\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.2526388472589888,\n" +
                "                                51.64080430793425\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.25264,\n" +
                "                                51.640806\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.2579628761320803,\n" +
                "                                51.63835382118516\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.2692251160239989,\n" +
                "                                51.63728041298178\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.271143,\n" +
                "                                51.637098\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.2711428833247263,\n" +
                "                                51.63709778150222\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.286317,\n" +
                "                                51.636244\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.299501,\n" +
                "                                51.631691\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.29996,\n" +
                "                                51.624413\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.293839,\n" +
                "                                51.624483\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.292836,\n" +
                "                                51.623658\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.293627,\n" +
                "                                51.62082\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.290411,\n" +
                "                                51.617827\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.303852,\n" +
                "                                51.611396\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.301102,\n" +
                "                                51.60679\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.294612,\n" +
                "                                51.605368\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.29046,\n" +
                "                                51.596187\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.2882316462873282,\n" +
                "                                51.59495924589054\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.29188,\n" +
                "                                51.5939\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.287825094747445,\n" +
                "                                51.59034872947627\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.288702,\n" +
                "                                51.590357\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.294155,\n" +
                "                                51.595129\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.304888,\n" +
                "                                51.595513\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.308296,\n" +
                "                                51.592729\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.30999,\n" +
                "                                51.595146\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.31282,\n" +
                "                                51.594564\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.315851,\n" +
                "                                51.599989\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.317229,\n" +
                "                                51.600599\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.325548,\n" +
                "                                51.601041\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.325039,\n" +
                "                                51.602682\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.329092,\n" +
                "                                51.605064\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.329921,\n" +
                "                                51.609751\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.332787,\n" +
                "                                51.613966\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.347047,\n" +
                "                                51.621018\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.345879,\n" +
                "                                51.625338\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.3602,\n" +
                "                                51.628401\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.366659,\n" +
                "                                51.625355\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.3666514995368937,\n" +
                "                                51.62534541762804\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.370408,\n" +
                "                                51.623739\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.37258,\n" +
                "                                51.620407\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.384741,\n" +
                "                                51.612727\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.393241,\n" +
                "                                51.612098\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.401533,\n" +
                "                                51.614036\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.4027541814392182,\n" +
                "                                51.613698121270986\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.4027816517991118,\n" +
                "                                51.61375892723019\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.401375,\n" +
                "                                51.61411\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.402113,\n" +
                "                                51.61578\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.401364,\n" +
                "                                51.621063\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.403032,\n" +
                "                                51.623139\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.41371,\n" +
                "                                51.626255\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.41375,\n" +
                "                                51.632245\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.418657,\n" +
                "                                51.63239\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.422558,\n" +
                "                                51.636371\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.434413,\n" +
                "                                51.633459\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.434315,\n" +
                "                                51.632245\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.442176,\n" +
                "                                51.632552\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.444719,\n" +
                "                                51.627702\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.440463,\n" +
                "                                51.623022\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.4424611888882493,\n" +
                "                                51.62190115987556\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.44603,\n" +
                "                                51.62257\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                -0.50156,\n" +
                "                                51.63172\n" +
                "                            ]\n" +
                "                        ]\n" +
                "                    ],\n" +
                "                    [\n" +
                "                        [\n" +
                "                            [\n" +
                "                                0.024313,\n" +
                "                                51.333504\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.017138,\n" +
                "                                51.333741\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.009876,\n" +
                "                                51.328545\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.016098,\n" +
                "                                51.317667\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.008837,\n" +
                "                                51.303564\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.01829,\n" +
                "                                51.299801\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.055085,\n" +
                "                                51.300871\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.049069,\n" +
                "                                51.317614\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.033196,\n" +
                "                                51.330038\n" +
                "                            ],\n" +
                "                            [\n" +
                "                                0.024313,\n" +
                "                                51.333504\n" +
                "                            ]\n" +
                "                        ]\n" +
                "                    ]\n" +
                "                ],\n" +
                "                \"type\": \"multipolygon\"}";


        Geometry geometry = toTest.fromJson(json, Geometry.class);

        assertThat(geometry, is(instanceOf(MultiPolygon.class)));

    }

    @Test
    public void shouldHandlePointEmpty() {

        Point source = gf.createPoint((Coordinate) null);

        Point parsed = toTest.fromJson(toTest.toJson(source), Point.class);

        assertThat(true, is(source.isEmpty()));
        assertGeometryEquals(parsed, null);
    }

    @Test
    public void shouldHandleMultiPolgonEmpty() {

        MultiPolygon source = gf.createMultiPolygon(null);

        MultiPolygon parsed = toTest.fromJson(toTest.toJson(source), MultiPolygon.class);

        assertThat(true, is(source.isEmpty()));
        assertGeometryEquals(parsed, null);
    }

    @Test
    public void shouldHandlePointNull() {

        Point source = null;

        Point parsed = toTest.fromJson(toTest.toJson(source), Point.class);

        assertGeometryEquals(parsed, source);
    }

    @Test
    public void shouldHandleMultiPolgonNull() {

        MultiPolygon source = null;

        MultiPolygon parsed = toTest.fromJson(toTest.toJson(source), MultiPolygon.class);

        assertGeometryEquals(parsed, source);
    }

    @Test
    public void shouldHandlePoint32633() {

        // Center of Vienna, Austria in UTM 33, precision in centimeter
        Point source = new GeometryFactory(new PrecisionModel(100), 32633).createPoint(new Coordinate(602010, 5340367));

        Point parsed = toTest.fromJson(toTest.toJson(source), Point.class);

        // this works with any GeometryFactory
        assertThat(parsed, equalTo(source));
        // should match the defined static GeometryFactory
        assertThat(parsed.getPrecisionModel(), equalTo(new PrecisionModel(PrecisionModel.FLOATING)));
        assertThat(parsed.getSRID(), equalTo(4326));
        // should not match with given source (as GeometryFactory of source and toTest differ!)
        assertThat(parsed.getPrecisionModel(), is(not(equalTo(source.getPrecisionModel()))));
        assertThat(parsed.getSRID(), is(not(equalTo(source.getSRID()))));
    }

    @Test
    public void shouldHandlePointWithDefaultConstructor() {

        Point source = new GeometryFactory().createPoint(new Coordinate(10, 20));

        Gson toTest = new GsonBuilder()
            .registerTypeAdapterFactory(new GeometryAdapterFactory())
            .registerTypeAdapterFactory(new JtsAdapterFactory()).create();

        Point parsed = toTest.fromJson(toTest.toJson(source), Point.class);

        assertGeometryEquals(parsed, source);
    }
}

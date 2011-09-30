/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package atd.pillage;

import java.util.concurrent.atomic.AtomicLong;

/**
 * This is a simple class to count stuff
 *
 * @author ATD
 */
public class Counter {

    private AtomicLong _counter = new AtomicLong(0L);

    public long incr(){  return _counter.incrementAndGet(); }
    public long incr( int i ){ return _counter.addAndGet(i); }
    public long value(){ return _counter.get(); }
    public void update(long l){ _counter.set(l); }
    public void reset() { _counter.set(0); }

    @Override
    public String toString() { return "Counter[" + _counter.get() + "]"; }

}

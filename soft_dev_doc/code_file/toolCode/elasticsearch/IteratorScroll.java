@Slf4j
public abstract class IteratorScroll {
    @Override
    public Stream<D> esStream() {
        return esStream(Query.findAll().setPageable(PageRequest.of(STREAM_PAGE_FORM,
                STREAM_PAGE_SIZE)), DEFALT_SCROLL_TIME_IN_MILLIS);
    }

    @Override
    public Stream<D> esStream(Query query, final long scrollTimeInMillis) {
        Pageable pageable = query.getPageable();
        int size = pageable.getPageSize();
        SearchScrollHits<D> scrollHits = this.elasticsearchRestTemplate.searchScrollStart(scrollTimeInMillis,
                query, clazz, getIndexCoordinates());
        Iterator<D> iterator = new Iterator<D>() {
            Iterator<SearchHit<D>> iterator = scrollHits.iterator();
            double nums = Math.ceil(scrollHits.getTotalHits() / (double) size);
            int pageNumber = 1;
            String scrollId = scrollHits.getScrollId();
            D next = null;

            @Override
            public boolean hasNext() {
                if (next != null) {
                    return true;
                } else {
                    boolean hasNext = iteratorHasNext();
                    if (hasNext) {
                        return hasNext;
                    } else {
                        nextScrollIterator();
                        hasNext = iteratorHasNext();
                        if (!hasNext) {
                            log.info("iterator over! clear scrollId: {}", scrollId);
                            elasticsearchRestTemplate.searchScrollClear(Arrays.asList(scrollId));
                        }
                        return hasNext;
                    }
                }
            }

            @Override
            public D next() {
                if (next != null || hasNext()) {
                    D line = next;
                    next = null;
                    return line;
                } else {
                    throw new NoSuchElementException();
                }
            }

            private boolean iteratorHasNext() {
                if (iterator == null) {
                    return false;
                }
                boolean hasNext = iterator.hasNext();
                if (hasNext) {
                    next = iterator.next().getContent();
                }
                return hasNext;
            }

            private void nextScrollIterator() {
                if (pageNumber < nums) {
                    SearchScrollHits<D> nextScroll =
                            elasticsearchRestTemplate.searchScrollContinue(scrollId, scrollTimeInMillis, clazz,
                                    getIndexCoordinates());
                    iterator = nextScroll.iterator();
                    pageNumber++;
                } else {
                    iterator = null;
                }
            }
        };
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iterator, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }
}

import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { get, postNoBody } from '../api'
import VideoCard from '../components/VideoCard'

export default function Channel() {
  const { id } = useParams()
  const [channel, setChannel] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    get(`/channels/${id}`)
      .then(setChannel)
      .catch((err) => setError(err.message || 'Failed to load channel'))
  }, [id])

  const toggleSubscription = async () => {
    setError('')
    try {
      const endpoint = channel?.subscribed ? `/channels/${id}/unsubscribe` : `/channels/${id}/subscribe`
      const result = await postNoBody(endpoint)
      if (result && result.subscriberCount != null) {
        setChannel((prev) => ({
          ...prev,
          subscribed: result.subscribed,
          subscriberCount: result.subscriberCount,
        }))
      }
    } catch (err) {
      setError(err.message || 'Subscription action failed')
    }
  }

  if (error) return <div className="card">Error: {error}</div>
  if (!channel) return <div>Loading...</div>

  return (
    <div className="channel-page">
      <div className="channel-banner card">
        <div className="channel-banner-info">
          <h1>{channel.channelInfo.username}</h1>
          <p>{channel.channelInfo.email || 'No email provided'}</p>
          <p>{channel.subscriberCount} subscribers</p>
        </div>
        <button className="button large" onClick={toggleSubscription}>
          {channel.subscribed ? 'Subscribed' : 'Subscribe'}
        </button>
      </div>

      <section className="channel-overview card">
        <h2>About</h2>
        <p>{channel.channelInfo.description || 'No channel description available.'}</p>
        <section className="pattern-panel">
          <h2>Proxy Pattern</h2>
          <p>
            Subscribe / unsubscribe chạy qua backend như một proxy service,
            nơi backend quyết định trạng thái đăng ký và phản hồi.
          </p>
        </section>
      </section>

      <section>
        <h2 className="section-title">Videos</h2>
        <div className="video-grid">
          {channel.videos.map((video) => (
            <VideoCard key={video.id} video={video} />
          ))}
        </div>
      </section>
    </div>
  )
}
